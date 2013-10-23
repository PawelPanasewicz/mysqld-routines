import sbt._
import Keys._
import sbt.Process
import scala.sys.process.{ProcessLogger, Process}

object Build extends Build {

   lazy val rootProj = Project("mysql-routines", file("."))
    .settings(
    version := "0.1-SNAPSHOT",
    organization := "pl.panasoft",
    organizationName := "pl.panasoft",
    scalaVersion := "2.10.3",
    crossScalaVersions := "2.9.2" :: "2.10.3" :: Nil,
    scalacOptions <++= scalaVersion map {
      case v: String if (v.startsWith("2.9")) => "-deprecation" :: "-unchecked" :: Nil
      case v: String if (v.startsWith("2.10")) => "-feature" :: "-language:implicitConversions" :: "-deprecation" :: "-unchecked" :: Nil
    },
    resolvers ++= commonResolvers,
    libraryDependencies <++= scalaVersion {
      case v: String if (v.startsWith("2.9")) => commonDependencies.filterNot(_.organization == "org.scalatest")  .filterNot(_.organization == "org.scalamock") :+ "org.scala-sbt" % "io" % "0.12.2"
      case v: String if (v.startsWith("2.10")) => commonDependencies :+ "org.scala-sbt" % "io" % "0.13.0"
    },
    testOptions in Test += Tests.Argument("-oD") //W - without color, F - show full stack traces, S - show short stack traces, D - show durations
  )
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

  val commonDependencies = List(
    "org.scalatest" % "scalatest_2.10.0" % "2.0.M5" % "test",
    ("org.scalamock" %% "scalamock-scalatest-support" % "3.0.1" exclude("org.scalatest", "scalatest_2.10")) % "test",
    "pl.panasoft" %% "pimps" % "0.1-SNAPSHOT"
  )

  val commonResolvers = List(
    "Sonatype repository releases" at "https://oss.sonatype.org/content/repositories/releases/",
    "Maven central repository" at "http://repo1.maven.org/maven2/",
    "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/",
    "Typesafe repository ivy-releases" at "http://repo.typesafe.com/typesafe/ivy-releases/",
    Resolver.url("Typesafe repository ivy pattern", url("http://repo.typesafe.com/typesafe/releases/"))(Resolver.ivyStylePatterns)
  )
}


