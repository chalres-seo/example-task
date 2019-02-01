package com.example.task.handler

import com.example.task.TaskMessage.{FailedTaskGroupB, Print, Sum}
import com.example.task.TastMessageGroupB
import com.example.utils.Retry
import com.typesafe.scalalogging.LazyLogging

object TaskGroupBHandler extends LazyLogging with Retry {
  def apply(task: TastMessageGroupB): Boolean = {
    Retry.retry {
      task match {
        case msg: Sum =>
          println(msg.ele.sum)
          true
        case msg: Print =>
          println(msg.str)
          true
        case _: FailedTaskGroupB =>
          logger.info("receive task message failed task group B")
          false
        case unknown =>
          logger.error(s"unknown task message group B type. task msg: ${unknown.getClass.getName}")
          false
      }
    }
  }
}
