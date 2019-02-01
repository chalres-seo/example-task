package com.example.utils

import java.util.concurrent.TimeUnit

import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration.Duration

object AppConfig extends LazyLogging {
  /** read custom application.conf */
//  private val conf: Config = this.readConfigFromFile("conf/application.conf")
  private val conf: Config = ConfigFactory.load().resolve()

  /** application config */
  val applicationName: String = conf.getString("application.name")
  val defaultTimeout: Long = conf.getLong("application.timeout")
  val defaultTimeUnit: TimeUnit = TimeUnit.MILLISECONDS

  /** retry config */
  val defaultBackOffTimeMillis: Long = conf.getLong("application.backOffTimeMills")
  val defaultMaxAttempt: Int = conf.getInt("application.maxAttempt")

  /** future config */
  val defaultFutureTimeWait: Duration = Duration.apply(conf.getLong("application.futureTimeWait"), TimeUnit.MILLISECONDS)
}