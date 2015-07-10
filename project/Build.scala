/***********************************************************************
 * Copyright (c) 2015 Azavea.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0 which
 * accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 ***********************************************************************/

import sbt._
import sbt.Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object Build extends Build {
  val defaultAssemblySettings =
    assemblySettings ++
  Seq(
    test in assembly := {},
    mergeStrategy in assembly <<= (mergeStrategy in assembly) {
      (old) => {
        case "reference.conf" => MergeStrategy.concat
        case "application.conf" => MergeStrategy.concat
        case "META-INF/MANIFEST.MF" => MergeStrategy.discard
        case "META-INF\\MANIFEST.MF" => MergeStrategy.discard
        case _ => MergeStrategy.first
      }
    }
  )

  override lazy val settings =
    super.settings ++ Seq(shellPrompt := { s => Project.extract(s).currentProject.id + " > " })

  lazy val root =
    Project("sfcurve", file("."))
      .aggregate(core)

  lazy val core: Project = 
    Project("core", file("core"))
      .settings(coreSettings: _*)

  lazy val coreSettings =
    Seq(
      name := "sfcurve",
      organization := "org.locationtech",
      version := "0.1.0-SNAPSHOT",
      scalaVersion := "2.11.7",
      scalacOptions ++= Seq(
        "-deprecation",
        "-unchecked",
        "-Yinline-warnings",
        "-language:implicitConversions",
        "-language:reflectiveCalls",
        "-language:postfixOps",
        "-language:existentials",
        "-feature"),
      libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest" % "2.2.4" % "test",
        "com.google.uzaygezen" % "uzaygezen-core" % "0.2"
        ),
      publishTo := Some(Resolver.file("file", new File(Path.userHome.absolutePath+"/.m2/repository")))
    ) ++ defaultAssemblySettings

  lazy val benchmarks: Project =
    Project("benchmarks", file("benchmarks"))
      .settings(benchmarksSettings: _*)
      .dependsOn(core)

  val benchmarkKey = AttributeKey[Boolean]("javaOptionsPatched")

  def benchmarksSettings =
    Seq(
      name := "benchmarks",
      organization := "org.locationtech",
      version := "0.1.0-SNAPSHOT",
      scalaVersion := "2.11.7",
      javaOptions += "-Xmx2G",

      // enable forking in both run and test
      fork := true,

      scalacOptions ++= Seq(
        "-deprecation",
        "-unchecked",
        "-Yinline-warnings",
        "-language:implicitConversions",
        "-language:reflectiveCalls",
        "-language:postfixOps",
        "-language:existentials",
        "-feature"),
      libraryDependencies ++= Seq(
        "com.google.code.caliper" % "caliper" % "1.0-SNAPSHOT" from "http://plastic-idolatry.com/jars/caliper-1.0-SNAPSHOT.jar",
        "com.google.guava" % "guava" % "r09",
        "com.google.code.java-allocation-instrumenter" % "java-allocation-instrumenter" % "2.0",
        "com.google.code.gson" % "gson" % "1.7.1"
      ),
      // custom kludge to get caliper to see the right classpath

      // we need to add the runtime classpath as a "-cp" argument to the
      // `javaOptions in run`, otherwise caliper will not see the right classpath
      // and die with a ConfigurationException unfortunately `javaOptions` is a
      // SettingsKey and `fullClasspath in Runtime` is a TaskKey, so we need to
      // jump through these hoops here in order to feed the result of the latter
      // into the former

      onLoad in Global ~= { previous => state =>
        previous {
          state.get(benchmarkKey) match {
            case None =>
              // get the runtime classpath, turn into a colon-delimited string
              Project
                .runTask(fullClasspath in Runtime in benchmarks, state)
                .get
                ._2
                .toEither match {
                case Right(x) =>
                  val classPath =
                    x.files
                      .mkString(":")
                  // return a state with javaOptionsPatched = true and javaOptions set correctly
                  Project
                    .extract(state)
                    .append(
                    Seq(javaOptions in (benchmarks, run) ++= Seq("-Xmx8G", "-cp", classPath)),
                      state.put(benchmarkKey, true)
                  )
                case _ => state
              }
            case Some(_) =>
              state // the javaOptions are already patched
          }
        }
      }
    )
}
