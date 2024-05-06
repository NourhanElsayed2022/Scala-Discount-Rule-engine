ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.13"

lazy val root = (project in file("."))
  .settings(
    name := "DiscountRules"
  )
libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.14.1"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.14.1"
