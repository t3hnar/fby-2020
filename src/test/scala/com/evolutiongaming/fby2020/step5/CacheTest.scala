package com.evolutiongaming.fby2020.step5

import cats.effect.{IO, Timer}
import cats.effect.concurrent.{Deferred, Ref}
import cats.implicits._
import com.evolutiongaming.fby2020.IOSuite
import org.scalatest.Succeeded
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration._
import scala.util.control.NoStackTrace

class CacheTest extends AsyncFunSuite with IOSuite with Matchers {

  val testError: Throwable = new RuntimeException with NoStackTrace

  test("getOrLoad handles errors") {
    val result = for {
      cache     <- Cache.of[Int, Int](100)
      deferred  <- Deferred[IO, Unit]
      ref       <- Ref[IO].of(0)
      getOrLoad  = cache.getOrLoad(0) {
        for {
          a <- ref.modify { a => (a + 1, a) }
          _ <- deferred.get
          _ <- IO.raiseError(testError)
        } yield a
      }
      fiber0    <- getOrLoad.start
      fiber1    <- getOrLoad.start
      _         <- Timer[IO].sleep(100.millis)
      _         <- deferred.complete(())
      value     <- fiber0.join.attempt
      _          = value shouldEqual testError.asLeft
      value     <- fiber1.join.attempt
      _          = value shouldEqual testError.asLeft
      value     <- ref.get
      _          = value shouldEqual 1
    } yield Succeeded
    result.unsafeRunSync()
  }
}
