package com.evolutiongaming.fby2020.step0

import cats.implicits._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class CacheTest extends AnyFunSuite with Matchers {

  test("cache") {
    val cache = Cache.of[Int, Int]
    cache.get(0) shouldEqual none
    cache.put(0, 0)
    cache.get(0) shouldEqual 0.some
    cache.remove(0)
    cache.get(0) shouldEqual none
  }
}
