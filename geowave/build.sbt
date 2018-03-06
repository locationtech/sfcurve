import Dependencies._

name := "sfcurve-geowave"
libraryDependencies ++= Seq(
  jsonLib,
  log4j12,
  uzaygezen,
  junit % "test",
  junitIface % "test"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")
