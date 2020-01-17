package com.evolutiongaming.fby2020.step3

import cats.effect.IO
import cats.implicits._
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.should.Matchers

class PartitionsTest extends AsyncFunSuite with Matchers {

  test("partitions") {
    Partitions
      .create[Int, String](3, _.toString.pure[IO])
      .map { partitions =>
        partitions(0) shouldEqual "0"
        partitions(1) shouldEqual "1"
        partitions(3) shouldEqual "0"
      }
      .unsafeToFuture()
  }
}
