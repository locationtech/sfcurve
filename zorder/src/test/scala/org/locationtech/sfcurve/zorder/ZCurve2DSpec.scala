package org.locationtech.sfcurve.zorder

import org.locationtech.sfcurve.SpaceFillingCurves
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ZCurve2DSpec extends AnyFunSpec with Matchers {
  describe("SPI access") {
    it("provides an SFC") {
      val sfc = SpaceFillingCurves("zorder", Map(ZOrderSFCProvider.RESOLUTION_PARAM -> Int.box(100)))
      sfc.isInstanceOf[ZCurve2D] should equal(true)
    }

    it("computes a covering set of ranges") {
      val sfc = SpaceFillingCurves("zorder", Map(ZOrderSFCProvider.RESOLUTION_PARAM -> Int.box(1024)))
      val ranges = sfc.toRanges(-80.0, 35.0, -75.0, 40.0, ZCurve2D.hints(maxRecurse = 32))

      ranges.length shouldBe 44
      val (l, r, contains) = ranges.head.tuple
      l shouldBe 197616
      r shouldBe 197631
      contains shouldBe true
    }
  }

}
