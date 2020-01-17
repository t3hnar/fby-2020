package com.evolutiongaming.fby2020.step3

import cats.effect.IO
import cats.implicits._

trait Partitions[K, V] {

  def apply(key: K): V
}

object Partitions {

  type PartitionNr = Int

  def create[K, V](
    nrOfPartitions: Int,
    partitionOf: PartitionNr => IO[V]
  ): IO[Partitions[K, V]] = {
    if (nrOfPartitions <= 0) {
      partitionOf(0).map { value => _: K => value }
    } else {
      (0 until nrOfPartitions)
        .toVector
        .traverse { partition => partitionOf(partition) }
        .map { values: Vector[V] =>
          (key: K) => {
            val partition = math.abs(key.hashCode() % nrOfPartitions)
            values(partition)
          }
        }
    }
  }
}