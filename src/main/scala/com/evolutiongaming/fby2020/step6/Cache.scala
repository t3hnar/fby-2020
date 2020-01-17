package com.evolutiongaming.fby2020.step6

import cats.effect.concurrent.{Deferred, Ref}
import cats.effect.{Concurrent, IO}
import cats.implicits._
import com.evolutiongaming.fby2020.step3.Partitions

// getOrLoad caches errors
trait Cache[K, V] {

  def get(key: K): IO[Option[V]]

  def getOrLoad(key: K)(value: => IO[V]): IO[V]

  def put(key: K, value: V): IO[Unit]

  def remove(key: K): IO[Unit]
}

object Cache {

  def create[K, V](implicit F: Concurrent[IO]): IO[Cache[K, V]] = {

    Ref[IO]
      .of(Map.empty[K, IO[V]])
      .map { ref =>

        new Cache[K, V] {

          def get(key: K) = ref.get.flatMap { _.get(key).sequence }

          def getOrLoad(key: K)(load: => IO[V]) = {
            ref
              .get
              .flatMap { map =>
                map
                  .get(key)
                  .fold {
                    Deferred[IO, IO[V]]
                      .flatMap { deferred =>
                        ref
                          .modify { map =>
                            map
                              .get(key)
                              .fold {
                                val value = load
                                  .attempt
                                  .flatMap { value =>
                                    deferred
                                      .complete(value.liftTo[IO])
                                      .flatMap { _ =>
                                        value
                                          .fold(_ => ref.update { _.removed(key) }, _ => ().pure[IO])
                                          .flatMap { _ => value.liftTo[IO] }
                                      }
                                  }
                                val map1 = map.updated(key, deferred.get.flatten)
                                (map1, value)
                              } { value =>
                                (map, value)
                              }
                          }
                          .flatten
                      }
                  } {
                    identity
                  }
              }
          }

          def put(key: K, value: V) = ref.update { _.updated(key, value.pure[IO]) }

          def remove(key: K) = ref.update { _.removed(key) }
        }
      }
  }

  def partitioned[K, V](nrOfPartitions: Int)(implicit F: Concurrent[IO]): IO[Cache[K, V]] = {
    Partitions
      .create[K, Cache[K, V]](nrOfPartitions, _ => create[K, V])
      .map { partitions =>
        new Cache[K, V] {

          def get(key: K) = partitions(key).get(key)

          def getOrLoad(key: K)(value: => IO[V]) = partitions(key).getOrLoad(key)(value)

          def put(key: K, value: V) = partitions(key).put(key, value)

          def remove(key: K) = partitions(key).remove(key)
        }
      }
  }
}