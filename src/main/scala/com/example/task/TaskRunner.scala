package com.example.task

import com.example.task.handler.{TaskGroupAHandler, TaskGroupBHandler}
import com.example.utils.{AppConfig, Retry}
import com.typesafe.scalalogging.LazyLogging

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TaskRunner(taskManager: TaskManager) extends LazyLogging {
  import TaskMessage._

  def start(parallelLevel: Int = 1): Vector[Future[(Int, Int)]] = {
    (1 to parallelLevel).map(_ => this.taskLoop).toVector
  }

  def awaitFinishAllTask(waitMillis: Long = AppConfig.defaultTimeout): Unit = {
    while (!taskManager.isEmptyTaskQueue) {
      logger.info(s"all tasks are not finished. wait $waitMillis millis")
      Thread.sleep(waitMillis)
    }
  }

  private def taskLoop: Future[(Int, Int)] = {
    Future {
      logger.info("task loop start.")

      @tailrec
      def loop(taskManager: TaskManager, currentCompletedTaskCount: Int, currentFailedTaskCount: Int): (Int, Int) = {
        if (!taskManager.isEmptyTaskQueue) {
          runTask() match {
            case Some(result) =>
              logger.info("pop task succeed.")
              val completedTaskCount = currentCompletedTaskCount + 1
              if (result) {
                logger.info(s"task succeed, completed task count: $completedTaskCount, failed task count: $currentFailedTaskCount")
                loop(taskManager, completedTaskCount, currentFailedTaskCount)
              } else {
                val failedTaskCount = currentFailedTaskCount + 1
                logger.info(s"task failed, completed task count: $completedTaskCount, failed task count: $failedTaskCount")
                loop(taskManager, completedTaskCount, failedTaskCount)
              }
            case None =>
              logger.info(s"failed pop task, completed message count: $currentCompletedTaskCount")
              loop(taskManager, currentCompletedTaskCount, currentFailedTaskCount)
          }
        } else (currentCompletedTaskCount, currentFailedTaskCount)
      }

      loop(taskManager, 0, 0)
    }
  }

  private def runTask(): Option[Boolean] = {
    logger.info("run task.")
    taskManager.popTask().map {
      case msg: TastMessageGroupA =>
        logger.info("popped task message group A. forward to Group A.")
        this.forwardGroupATask(msg)
      case msg: TastMessageGroupB =>
        logger.info("popped task message group B. forward to Group B.")
        this.forwardGroupBTask(msg)
      case unknown =>
        logger.error(s"unknown task message type, task msg: ${unknown.getClass.getName}")
        false
    }
  }

  private def forwardGroupATask(task: TastMessageGroupA): Boolean = {
    logger.info("msg forwarded group A task.")
    logger.info(s"receive task message. task msg: ${task.getClass.getName}")
    try {
      TaskGroupAHandler(task)
    } catch {
      case e: Exception =>
        logger.error(s"failed task msg handler. task msg: ${e.getMessage}, msg: ${e.getMessage}", e)
        false
    }
  }

  private def forwardGroupBTask(task: TastMessageGroupB): Boolean = {
    logger.info("msg forwarded group B task.")
    logger.info(s"receive task message. task msg: ${task.getClass.getName}")
    try {
      TaskGroupBHandler(task)
    } catch {
      //      case e: Exception =>
      //        logger.error(s"failed task msg handler. task msg: ${task.getClass.getName}, msg: ${e.getMessage}", e)
      //        false
      case e: Exception => ???
    }
  }

  private def taskExceptionHander(e: Exception) = {
//    case e: Exception =>
//      logger.error(s"failed task msg handler. task msg: ${task.getClass.getName}, msg: ${e.getMessage}", e)
//      false
  }
}

object TaskRunner extends LazyLogging {
  def apply(taskManager: TaskManager): TaskRunner = new TaskRunner(taskManager)
}
