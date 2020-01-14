package com.evolutiongaming.fby2020.step1

import cats.effect.IO
import cats.effect.concurrent.Ref

// in order to implement getOrLoad
trait Cache[K, V] {

  def get(key: K): IO[Option[V]]

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
}