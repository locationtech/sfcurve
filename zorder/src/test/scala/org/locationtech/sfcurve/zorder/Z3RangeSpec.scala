package org.locationtech.sfcurve.zorder

import org.scalatest._

class Z3RangeSpec extends FunSpec with Matchers {

  describe("Z3Range") {

    val zmin = Z3(2, 2, 0)
    val zmax = Z3(3, 6, 0)
    val range = Z3Range(zmin, zmax)

    it("require ordered min and max") {
      Z3Range(Z3(2, 2, 0), Z3(1, 4, 0)) // should be valid
      intercept[IllegalArgumentException] {
        Z3Range(zmax, zmin)
      }
    }

    it("for uncuttable ranges") {
      val range = Z3Range(zmin, zmin)
      Z3.cut(range, Z3(0, 0, 0), inRange = false) shouldBe empty
    }
    it("for out of range zs")  {
      val zcut = Z3(5, 1, 0)
      Z3.cut(range, zcut, inRange = false) shouldEqual
        List(Z3Range(zmin, Z3(3, 3, 0)), Z3Range(Z3(2, 4, 0), zmax))
    }

    it("support length") {
      range.length shouldEqual 130
    }

    it("support overlaps") {
      range.overlapsInUserSpace(range) shouldBe true
      range.overlapsInUserSpace(Z3Range(Z3(3, 0, 0), Z3(3, 2, 0))) shouldBe true
      range.overlapsInUserSpace(Z3Range(Z3(0, 0, 0), Z3(2, 2, 0))) shouldBe true
      range.overlapsInUserSpace(Z3Range(Z3(1, 6, 0), Z3(4, 6, 0))) shouldBe true
      range.overlapsInUserSpace(Z3Range(Z3(2, 0, 0), Z3(3, 1, 0))) shouldBe false
      range.overlapsInUserSpace(Z3Range(Z3(4, 6, 0), Z3(6, 7, 0))) shouldBe false
    }

    it("support contains ranges")  {
      range.containsInUserSpace(range) shouldBe true
      range.containsInUserSpace(Z3Range(Z3(2, 2, 0), Z3(3, 3, 0))) shouldBe true
      range.containsInUserSpace(Z3Range(Z3(3, 5, 0), Z3(3, 6, 0))) shouldBe true
      range.containsInUserSpace(Z3Range(Z3(2, 2, 0), Z3(4, 3, 0))) shouldBe false
      range.containsInUserSpace(Z3Range(Z3(2, 1, 0), Z3(3, 3, 0))) shouldBe false
      range.containsInUserSpace(Z3Range(Z3(2, 1, 0), Z3(3, 7, 0))) shouldBe false
    }
  }
}
