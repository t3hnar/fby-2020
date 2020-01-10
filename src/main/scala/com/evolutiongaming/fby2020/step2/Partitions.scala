package com.evolutiongaming.fby2020.step2

import cats.effect.IO
import cats.implicits._

trait Partitions[K, V] {

  def apply(key: K): V
}

object Partitions {

  def of[K, V](
    nrOfPartitions: Int,
    valueOf: Int => IO[V]
  ): IO[Partitions[K, V]] = {
    if (nrOfPartitions <= 0) {
      valueOf(0).map { value => _: K => value }
    } else {
      (0 until nrOfPartitions)
        .toVector
        .traverse { partition => valueOf(partition) }
        .map { values =>
          (key: K) => {
            val partition = math.abs(key.hashCode() % nrOfPartitions)
            values(partition)
          }
        }
    }
  }
}