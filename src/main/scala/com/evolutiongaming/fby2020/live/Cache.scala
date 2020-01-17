package com.evolutiongaming.fby2020.live

import cats.implicits._
import cats.effect.IO

trait Cache[K, V] {

  def get(key: K): Option[V]

  def put(key: K, value: V): Unit

  def getOrLoad(key: K)(value: => IO[V]): IO[V]

  def remove(key: K): IO[Unit]
}