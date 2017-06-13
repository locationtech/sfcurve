import Dependencies._

name := "sfcurve-geowave-index"
libraryDependencies ++= Seq(
  findbugs,
  jsonLib,
  log4j12,
  uzaygezen,
  junit,
  junitIface
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v")
