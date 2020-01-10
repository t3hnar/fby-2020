package com.evolutiongaming.fby2020.live

trait Cache[K, V] {

  def get(key: K): Option[V]

  def put(key: K, value: V): Unit

  def remove(key: K): Unit
}

object Cache {

}

