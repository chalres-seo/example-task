package com.example.task.handler

import com.example.task.TaskMessage.{FailedTaskGroupA, Ping, Pong}
import com.example.task.TastMessageGroupA
import com.example.utils.Retry
import com.typesafe.scalalogging.LazyLogging

object TaskGroupAHandler extends LazyLogging with Retry {
  def apply(task: TastMessageGroupA): Boolean = {
    Retry.retry {
      task match {
        case _: Ping =>
          println("pong")
          true
        case _: Pong =>
          logger.info("receive task message pong.")
          true
        case _: FailedTaskGroupA =>
          logger.info("receive task message failed task group A")
          false
        case unknown =>
          logger.error(s"unknown task message group A type. task msg: ${unknown.getClass.getName}")
          false
      }
    }
  }
}
