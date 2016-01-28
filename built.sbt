import Dependencies._

lazy val commonSettings = Seq(
  version := Version.sfcurve,
  scalaVersion := Version.scala,
  crossScalaVersions := Version.crossScala,
  description := "SFCurve is a space filling curve library for the JVM.",
  organization := "org.locationtech.sfcurve",
  licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html")),
  homepage := Some(url("https://github.com/locationtech/sfcurve")),
  scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked",
    "-Yinline-warnings",
    "-language:implicitConversions",
    "-language:reflectiveCalls",
    "-language:postfixOps",
    "-language:existentials",
    "-feature"),
  publishTo := Some("Sonatype LocationTech Thirdparty Nexus" at "https://repo.locationtech.org/content/repositories/thirdparty"),
  //publishTo := Some(Resolver.file("file", new File(Path.userHome.absolutePath+"/.m2/repository"))),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },

  pomExtra := (
    <scm>
      <url>https://github.com/locationtech/sfcurve</url>
      <connection>scm:https://github.com/locationtech/sfcurve</connection>
      </scm>
      <developers>
      <developer>
      <id>jnh5y</id>
      <name>Jim Hughes</name>
      <url>http://github.com/jnh5y/</url>
        </developer>
      <developer>
      <id>lossyrob</id>
      <name>Rob Emanuele</name>
      <url>http://github.com/lossyrob/</url>
        </developer>
      </developers>),
  shellPrompt := { s => Project.extract(s).currentProject.id + " > " }
) ++ net.virtualvoid.sbt.graph.Plugin.graphSettings

lazy val root =
  Project("sfcurve", file("."))
    .aggregate(api, zorder, hilbert)
    .settings(commonSettings: _*)

lazy val api: Project =
  Project("api", file("api"))
    .settings(commonSettings: _*)

lazy val zorder: Project =
  Project("zorder", file("zorder"))
    .settings(commonSettings: _*)
    .dependsOn(api)

lazy val hilbert: Project =
  Project("hilbert", file("hilbert"))
    .settings(commonSettings: _*)
    .dependsOn(api)


lazy val benchmarks: Project =
  Project("benchmarks", file("benchmarks"))
    .settings(commonSettings: _*)
    .dependsOn(api, zorder, hilbert)
