package org.locationtech.sfcurve

trait SpaceFillingCurveProvider {
  def canProvide(name: String): Boolean
  def build2DSFC(args: Map[String, java.io.Serializable]): SpaceFillingCurve2D
}
