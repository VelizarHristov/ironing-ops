name := """ironing-ops"""
organization := "ops.ironing"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.5"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

libraryDependencies += "com.typesafe.play"  %% "play-slick" % "3.0.3"
libraryDependencies += "org.postgresql"     %  "postgresql"    % "42.2.2"
libraryDependencies ++= Seq(evolutions, jdbc)
