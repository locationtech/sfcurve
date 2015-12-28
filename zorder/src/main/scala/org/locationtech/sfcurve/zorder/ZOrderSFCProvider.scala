package org.locationtech.sfcurve.zorder

import org.locationtech.sfcurve.{SpaceFillingCurve2D, SpaceFillingCurveProvider}

class ZOrderSFCProvider extends SpaceFillingCurveProvider {
  override def canProvide(name: String): Boolean = name == "zorder"

  override def build2DSFC(args: Map[String, java.io.Serializable]): SpaceFillingCurve2D =
    new ZCurve2D(args(ZOrderSFCProvider.RESOLUTION_PARAM).asInstanceOf[Int])
}

object ZOrderSFCProvider {
  val RESOLUTION_PARAM = "zorder.resolution"
}