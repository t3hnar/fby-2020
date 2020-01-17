package com.evolutiongaming.fby2020.step6

import cats.effect.{IO, Timer}
import cats.effect.concurrent.{Deferred, Ref}
import cats.implicits._
import com.evolutiongaming.fby2020.IOSuite
import org.scalatest.Succeeded
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.should.Matchers

import scala.util.control.NoStackTrace

class CacheTest extends AsyncFunSuite with IOSuite with Matchers {

  val testError: Throwable = new RuntimeException with NoStackTrace

  test("getOrLoad does not leak resources in case of errors") {
    val result = for {
      cache     <- Cache.partitioned[Int, Int](100)
      value     <- cache.getOrLoad(0) { testError.raiseError[IO, Int] }.attempt
      _          = value shouldEqual testError.asLeft
      value     <- cache.getOrLoad(0) { 0.pure[IO] }.attempt
      _          = value shouldEqual 0.asRight
    } yield Succeeded
    result.unsafeRunSync()
  }
}