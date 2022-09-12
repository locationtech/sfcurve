package org.locationtech.sfcurve

import java.util.ServiceLoader

object SpaceFillingCurves {

  import scala.collection.JavaConverters._

  lazy val loader = ServiceLoader.load(classOf[SpaceFillingCurveProvider])

  def apply(name: String, args: Map[String, java.io.Serializable]): SpaceFillingCurve2D = {
    val provider =
      loader.asScala.find(_.canProvide(name)).getOrElse(throw new RuntimeException(s"Cannot find provider for type: $name"))
    provider.build2DSFC(args)
  }

}
