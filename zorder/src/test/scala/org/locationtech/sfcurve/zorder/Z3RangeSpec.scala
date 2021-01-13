package org.locationtech.sfcurve.zorder

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class Z3RangeSpec extends AnyFunSpec with Matchers  {

  describe("Z3Range") {

    val zmin = Z3(2, 2, 0)
    val zmax = Z3(3, 6, 0)
    val range = ZRange(zmin, zmax)

    it("require ordered min and max") {
      ZRange(Z3(2, 2, 0), Z3(1, 4, 0)) // should be valid
      intercept[IllegalArgumentException] {
        ZRange(zmax, zmin)
      }
    }

    it("for uncuttable ranges") {
      val range = ZRange(zmin, zmin)
      Z3.cut(range, Z3(0, 0, 0).z, inRange = false) shouldBe empty
    }
    it("for out of range zs")  {
      val zcut = Z3(5, 1, 0).z
      Z3.cut(range, zcut, inRange = false) shouldEqual
        List(ZRange(zmin, Z3(3, 3, 0)), ZRange(Z3(2, 4, 0), zmax))
    }

    it("support length") {
      range.length shouldEqual 130
    }

    it("support overlaps") {
      Z3.overlaps(range, range) shouldBe true
      Z3.overlaps(range, ZRange(Z3(3, 0, 0), Z3(3, 2, 0))) shouldBe true
      Z3.overlaps(range, ZRange(Z3(0, 0, 0), Z3(2, 2, 0))) shouldBe true
      Z3.overlaps(range, ZRange(Z3(1, 6, 0), Z3(4, 6, 0))) shouldBe true
      Z3.overlaps(range, ZRange(Z3(2, 0, 0), Z3(3, 1, 0))) shouldBe false
      Z3.overlaps(range, ZRange(Z3(4, 6, 0), Z3(6, 7, 0))) shouldBe false
    }

    it("support contains ranges")  {
      Z3.contains(range, range) shouldBe true
      Z3.contains(range, ZRange(Z3(2, 2, 0), Z3(3, 3, 0))) shouldBe true
      Z3.contains(range, ZRange(Z3(3, 5, 0), Z3(3, 6, 0))) shouldBe true
      Z3.contains(range, ZRange(Z3(2, 2, 0), Z3(4, 3, 0))) shouldBe false
      Z3.contains(range, ZRange(Z3(2, 1, 0), Z3(3, 3, 0))) shouldBe false
      Z3.contains(range, ZRange(Z3(2, 1, 0), Z3(3, 7, 0))) shouldBe false
    }
  }
}
