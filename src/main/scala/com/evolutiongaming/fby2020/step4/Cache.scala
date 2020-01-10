package com.evolutiongaming.fby2020.step4

import cats.effect.{Concurrent, IO}
import cats.effect.concurrent.{Deferred, Ref}
import cats.implicits._
import com.evolutiongaming.fby2020.step2.Partitions

trait Cache[K, V] {

  def get(key: K): IO[Option[V]]

  def getOrLoad(key: K)(value: => IO[V]): IO[V]

  def put(key: K, value: V): IO[Unit]

  def remove(key: K): IO[Unit]
}

object Cache {

  def of[K, V](implicit F: Concurrent[IO]): IO[Cache[K, V]] = {

    Ref[IO]
      .of(Map.empty[K, IO[V]])
      .map { ref =>

        new Cache[K, V] {

          def get(key: K) = ref.get.flatMap { _.get(key).sequence }

          def getOrLoad(key: K)(load: => IO[V]) = {

            def loadAndPut: IO[V] = {
              for {
                deferred <- Deferred[IO, V]
                value    <- ref.modify { map =>
                  map.get(key) match {
                    case Some(value) => (map, value)
                    case None        =>
                      val value = load.flatMap { value => deferred.complete(value) as value }
                      (map.updated(key, deferred.get), value)
                  }
                }
                value <- value
              } yield value
            }

            ref
              .get
              .flatMap { _.get(key).fold { loadAndPut } { identity } }
          }

          def put(key: K, value: V) = ref.update { _.updated(key, value.pure[IO]) }

          def remove(key: K) = ref.update { _.removed(key) }
        }
      }
  }

  def of[K, V](nrOfPartitions: Int)(implicit F: Concurrent[IO]): IO[Cache[K, V]] = {
    Partitions
      .of[K, Cache[K, V]](nrOfPartitions, _ => of[K, V])
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
