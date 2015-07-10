resolvers ++= Seq(
  Classpaths.sbtPluginReleases,
  Opts.resolver.sonatypeReleases
)

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.11.2")

