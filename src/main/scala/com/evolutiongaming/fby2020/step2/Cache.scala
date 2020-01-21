package com.evolutiongaming.fby2020.step2

import cats.effect.IO
import cats.effect.concurrent.Ref
import cats.implicits._

// IO
trait Cache[K, V] {

  def get(key: K): IO[Option[V]]

  def getOrLoad(key: K)(load: => IO[V]): IO[V]

  def put(key: K, value: V): IO[Unit]

  def remove(key: K): IO[Unit]
}

object Cache {

  def create[K, V]: IO[Cache[K, V]] = {
    Ref[IO]
      .of(Map.empty[K, V])
      .map { ref =>

        new Cache[K, V] {

          def get(key: K) = ref.get.map { _.get(key) }

          def getOrLoad(key: K)(load: => IO[V]) = {
            ref
              .get
              .flatMap { map =>
                map
                  .get(key)
                  .fold {
                    load.flatMap { value =>
                      ref
                        .update { _.updated(key, value) }
                        .map { _ => value }
                    }
                  } {
                    _.pure[IO]
                  }
              }
          }

          def put(key: K, value: V) = ref.update { _.updated(key, value) }

          def remove(key: K) = ref.update { _.removed(key) }
        }
      }
  }
}
