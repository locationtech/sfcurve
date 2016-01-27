import Dependencies._

name := "sfcurve-hilbert"
libraryDependencies ++= Seq(
  uzaygezen,
  scalaTest % "test"
)
scalacOptions ++= Seq("-optimize")
