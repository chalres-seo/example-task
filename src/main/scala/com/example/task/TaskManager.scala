package com.example.task

import java.util.concurrent.{LinkedBlockingQueue, TimeUnit}

import com.example.utils.AppConfig
import com.typesafe.scalalogging.LazyLogging

class TaskManager extends LazyLogging {
  private val defaultTimeout = AppConfig.defaultTimeout
  private val defaultTimeUnit = AppConfig.defaultTimeUnit

  private val taskQueue: LinkedBlockingQueue[TaskMessage] = new LinkedBlockingQueue[TaskMessage]

  def pushTask(task: TaskMessage, timeout: Long = defaultTimeout, timeUnit: TimeUnit = defaultTimeUnit): Boolean = {
    logger.info(s"push task. msg type: ${task.getClass.getName}")
    try {
      taskQueue.offer(task, timeout, timeUnit)
    } catch {
      case e: Exception =>
        logger.error(s"failed offer task to queue. msg: ${e.getMessage}")
        false
    }
  }
  def popTask(timeout: Long = defaultTimeout, timeUnit: TimeUnit = defaultTimeUnit): Option[TaskMessage] = {
    logger.info("pop task.")
    try {
      Option(taskQueue.poll(timeout, timeUnit))
    } catch {
      case e: Exception =>
        logger.error(s"failed pop task from queue. msg: ${e.getMessage}")
        None
    }
  }

  def isEmptyTaskQueue: Boolean = taskQueue.isEmpty
  def isFullTaskQueue: Boolean = taskQueue.remainingCapacity() < 1
  def getTaskCount: Int = taskQueue.size()
}

object TaskManager extends LazyLogging {
  private val instance: TaskManager = this.createInstance

  def getInstance: TaskManager = instance

  private def createInstance: TaskManager = {
    logger.info("create TaskManager object.")
    new TaskManager
  }
}