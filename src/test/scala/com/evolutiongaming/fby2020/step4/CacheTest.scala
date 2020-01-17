package com.evolutiongaming.fby2020.step4

import cats.effect.{IO, Timer}
import cats.effect.concurrent.{Deferred, Ref}
import cats.implicits._
import com.evolutiongaming.fby2020.IOSuite
import org.scalatest.Succeeded
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration._

class CacheTest extends AsyncFunSuite with IOSuite with Matchers {

  test("getOrLoad does not load concurrently for the same key") {
    val result = for {
      cache     <- Cache.partitioned[Int, Int](100)
      deferred  <- Deferred[IO, Unit]
      ref       <- Ref[IO].of(0)
      getOrLoad  = cache.getOrLoad(0) {
        for {
          a <- ref.modify { a => (a + 1, a) }
          _ <- deferred.get
        } yield a
      }
      fiber0    <- getOrLoad.start
      fiber1    <- getOrLoad.start
      _         <- Timer[IO].sleep(100.millis)
      _         <- deferred.complete(())
      value     <- fiber0.join
      _          = value shouldEqual 0
      value     <- fiber1.join
      _          = value shouldEqual 0
    } yield Succeeded
    result.unsafeRunSync()
  }
}
