package com.evolutiongaming.fby2020.step3

import cats.implicits._
import org.scalatest.Succeeded
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.should.Matchers

class CacheTest extends AsyncFunSuite with Matchers {

  test("partitioned") {
    val result = for {
      cache <- Cache.partitioned[Int, Int](100)
      value <- cache.get(0)
      _      = value shouldEqual none
      _     <- cache.put(0, 0)
      value <- cache.get(0)
      _      = value shouldEqual 0.some
      _     <- cache.remove(0)
      value <- cache.get(0)
      _      = value shouldEqual none
    } yield Succeeded
    result.unsafeRunSync()
  }
}
