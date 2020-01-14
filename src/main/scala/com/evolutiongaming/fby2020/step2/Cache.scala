package com.evolutiongaming.fby2020.step2

import cats.effect.IO
import cats.effect.concurrent.Ref

// partitions
trait Cache[K, V] {

  def get(key: K): IO[Option[V]]

  // def getOrLoad(key: K)(value: => V): V
  // VS
  // def getOrLoad(key: K): V

  def put(key: K, value: V): IO[Unit]

  def remove(key: K): IO[Unit]
}

object Cache {

  def of[K, V]: IO[Cache[K, V]] = {
    Ref[IO]
      .of(Map.empty[K, V])
      .map { ref =>

        new Cache[K, V] {

          def get(key: K) = ref.get.map { _.get(key) }

          def put(key: K, value: V) = ref.update { _.updated(key, value) }

          def remove(key: K) = ref.update { _.removed(key) }
        }
      }
  }

  def of[K, V](nrOfPartitions: Int): IO[Cache[K, V]] = {
    Partitions
      .of[K, Cache[K, V]](nrOfPartitions, _ => of[K, V])
      .map { partitions =>
        new Cache[K, V] {

          def get(key: K) = partitions(key).get(key)

          def put(key: K, value: V) = partitions(key).put(key, value)

          def remove(key: K) = partitions(key).remove(key)
        }
      }
  }
}
