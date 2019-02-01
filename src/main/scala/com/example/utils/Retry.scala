package com.example.utils

import com.typesafe.scalalogging.LazyLogging

import scala.annotation.tailrec

trait Retry extends LazyLogging {
  private val defaultBackOffTimeInMillis: Long = AppConfig.defaultBackOffTimeMillis
  private val defaultMaxAttemptCount: Int = AppConfig.defaultMaxAttempt

  private def backOff(millis: Long): Unit = {
    logger.debug(s"back off $millis millis.")
    threadSleep(millis)
  }

  private  def backOff(): Unit = {
    threadSleep(defaultBackOffTimeInMillis)
  }

  private def threadSleep(): Unit = {
    this.threadSleep(defaultBackOffTimeInMillis)
  }

  private def threadSleep(millis: Long): Unit = {
    try {
      Thread.sleep(millis)
    } catch {
      case e: InterruptedException =>
        logger.error(s"interrupted sleep. msg: ${e.getMessage}", e)
      case e: Exception =>
        logger.error(s"unknown exception thread sleep, msg: ${e.getMessage}", e)
    }
  }

  def retry[T](fn: => T): T = this.retry(defaultMaxAttemptCount)(fn)

  @throws(classOf[Exception])
  def retry[T](attemptCount: Int)(fn: => T): T = {
    @tailrec
    def loop(remainAttemptCount: Int): T = {
      try {
        fn
      } catch {
        case e: Exception =>
          if (remainAttemptCount > 0) {
            logger.warn(s"backOff $defaultBackOffTimeInMillis millis for retry. retry count remain: $remainAttemptCount")
            loop(remainAttemptCount - 1)
          } else {
            logger.error(s"attempts has been exceeded. msg: ${e.getMessage}", e)
            throw e
          }
      }
    }
    loop(defaultMaxAttemptCount)
  }
}

object Retry extends LazyLogging with Retry