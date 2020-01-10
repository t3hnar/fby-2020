package com.evolutiongaming.fby2020

import cats.effect.{ContextShift, IO, Timer}

import scala.concurrent.ExecutionContext

trait IOSuite {

  def executor: ExecutionContext = ExecutionContext.global

  implicit val contextShift: ContextShift[IO] = IO.contextShift(executor)

  implicit val timer: Timer[IO] = IO.timer(executor)
}
