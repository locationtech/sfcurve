SFCurve
=====

[![Join the chat at https://gitter.im/locationtech/sfcurve](https://badges.gitter.im/locationtech/sfcurve.svg)](https://gitter.im/locationtech/sfcurve?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

![sfcurve-space-diagram](https://cloud.githubusercontent.com/assets/2320142/6543539/449db6e2-c4ed-11e4-865a-584e056b5469.png)

### What is SFCurve?

This library represents a collaborative attempt to create a solid, robust and modular library for dealing with space filling curves on the JVM.

To read up on the collaborative effort to define the interface, and to participate in the discussion, see [Issue #3](https://github.com/geotrellis/curve/issues/3).

### Where can I learn more about the project?

A more detailed account of the origin and intention can be found in the [proposal of the SFCurve project submitted to LocationTech](http://www.locationtech.org/proposals/sfcurve). You can also find more information by contacting any of the current collaborators, or by asking about it on the [GeoTrellis](https://github.com/geotrellis/geotrellis), [GeoMesa](https://github.com/locationtech/geomesa), or [GeoWave](https://github.com/ngageoint/geowave) mailing lists.

### Can I use SFCurve now?

This library is a complete work in progress, and is __NOT__ recommended for current use. In the future, though, we hope to be the definitive library for working with space filling curves on the JVM. If you have ideas on how to get us there, please participate!

### Working with SFCurve

The build tool used in this project is [sbt](http://www.scala-sbt.org/). A script is included that will download the necessary `sbt` software, so you do not need `sbt` installed on the machine to work with this project.

To drop into the `sbt` console, where you can execute various commands, simply type

```
> ./sbt
```

in your shell.

In the below examples `>`, `bechmarks >` etc. represents the sbt console prompt.

Once in the `sbt` console, to compile the code, issue the command:

```
> compile
```

To test the code:

```
> test
```

To start a scala console which has the core sfcurve code in the classpath:

```
> console
```

To run the benchmarks, drop into the benchmark subproject using the `project` command, and run:

```
> project benchmarks
benchmarks > run
```

### Publishing snapshot binaries

If you run the `publish-local` sbt command, the subproject artifacts will be published to the local `ivy2` cache.

If you run the `publish` sbt command, the subproject artifacts will be published to the local maven2 repository.

This will publish artifacts for the latest scala version. If you want other scala versions, you can add a `+` in front of the command to do cross-version commands. So `+publish-local` will publish scala 2.10 and 2.11 artifacts to the local ivy2 cache.

### Showing the dependency graph

Run `dependencyGraph` in the subproject sbt console.
