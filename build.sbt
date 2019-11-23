import Dependencies._

name := "fby-2020"

organization := "com.github.t3hnar"

homepage := Some(new URL("http://github.com/t3hnar/fby-2020"))

startYear := Some(2019)

scalaVersion := "2.13.1"

resolvers += Resolver.bintrayRepo("evolutiongaming", "maven")

libraryDependencies ++= Seq(
  Cats.core,
  Cats.effect,
  `cats-helper`,
  scalatest % Test)

licenses := Seq(("MIT", url("https://opensource.org/licenses/MIT")))

scalacOptions in(Compile, doc) ++= Seq("-groups", "-implicits", "-no-link-warnings")