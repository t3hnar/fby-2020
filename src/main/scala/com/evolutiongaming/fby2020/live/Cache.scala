package com.evolutiongaming.fby2020.live

import cats.implicits._
import cats.effect.IO
import cats.effect.concurrent.Ref

import scala.collection.concurrent.TrieMap

trait Cache[K, V] {

  def get(key: K): Option[V]

  def put(key: K, value: V): Unit

  def getOrLoad(key: K)(load: => V): V

  def remove(key: K): Unit
}

object Cache {

  def create[K, V]: Cache[K, V] = {

    new Cache[K, V] {

      def get(key: K) = ???

      def getOrLoad(key: K)(load: => V) = ???

      def put(key: K, value: V) = ???

      def remove(key: K) = ???
    }
  }
}