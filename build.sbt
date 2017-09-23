name := """ironing-ops"""
organization := "ops.ironing"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies ++= Seq(evolutions, jdbc)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "ops.ironing.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "ops.ironing.binders._"
