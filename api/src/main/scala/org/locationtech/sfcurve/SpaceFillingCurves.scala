package org.locationtech.sfcurve

import java.util.ServiceLoader

import scala.collection.JavaConversions._

object SpaceFillingCurves {

  lazy val loader = ServiceLoader.load(classOf[SpaceFillingCurveProvider])

  def apply(name: String, args: Map[String, java.io.Serializable]): SpaceFillingCurve2D = {
    val provider =
      loader.find(_.canProvide(name)).getOrElse(throw new RuntimeException(s"Cannot find provider for type: $name"))
    provider.build2DSFC(args)
  }

}
