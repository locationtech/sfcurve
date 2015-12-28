package org.locationtech.sfcurve.zorder

import org.locationtech.sfcurve.SpaceFillingCurves
import org.scalatest.{FunSpec, Matchers}

class ZCurve2DSpec extends FunSpec with Matchers {
  describe("SPI access") {
    it("provides an SFC") {
      val sfc = SpaceFillingCurves("zorder", Map(ZOrderSFCProvider.RESOLUTION_PARAM -> Int.box(100)))
      sfc.isInstanceOf[ZCurve2D] should equal(true)
    }
  }

}
