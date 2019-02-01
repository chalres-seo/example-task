package com.example.task

import java.util.UUID

trait TaskMessage

trait TastMessageGroupA extends TaskMessage

trait TastMessageGroupB extends TaskMessage

object TaskMessage {
  case class Ping() extends TastMessageGroupA
  case class Pong() extends TastMessageGroupA
  case class FailedTaskGroupA() extends TastMessageGroupA
  case class ForException() extends TastMessageGroupA
  case class Sum(ele: Int*) extends TastMessageGroupB
  case class Print(str: String) extends TastMessageGroupB
  case class FailedTaskGroupB() extends TastMessageGroupB
}