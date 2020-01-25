package com.evolutiongaming.fby2020.step0

import scala.collection.concurrent.TrieMap

// concurrency
trait Cache[K, V] {

  def get(key: K): Option[V]

  def getOrLoad(key: K)(value: => V): V

  def put(key: K, value: V): Unit

  def remove(key: K): Unit
}

object Cache {

  def create[K, V]: Cache[K, V] = {

    val map = TrieMap.empty[K, V]

    new Cache[K, V] {

      def get(key: K) = map.get(key)

      def getOrLoad(key: K)(value: => V) = map.getOrElseUpdate(key, value)

      def put(key: K, value: V) = map.put(key, value)

      def remove(key: K) = map.remove(key)
    }
  }
}