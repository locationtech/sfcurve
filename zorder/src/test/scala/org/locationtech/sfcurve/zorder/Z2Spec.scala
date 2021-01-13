/***********************************************************************
 * Copyright (c) 2015 Azavea.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0 which
 * accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 ***********************************************************************/

package org.locationtech.sfcurve.zorder

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers


class Z2Spec extends AnyFunSpec with Matchers {

  describe("Z2 encoding") {
    it("interlaces bits"){
      Z2(1,0).z should equal(1)
      Z2(2,0).z should equal(4)
      Z2(3,0).z should equal(5)
      Z2(0,1).z should equal(2)
      Z2(0,2).z should equal(8)
      Z2(0,3).z should equal(10)

    }

    it("deinterlaces bits") {
      Z2(23,13).decode  should equal(23, 13)
      Z2(Int.MaxValue, 0).decode should equal(Int.MaxValue, 0)
      Z2(0, Int.MaxValue).decode should equal(0, Int.MaxValue)
      Z2(Int.MaxValue, Int.MaxValue).decode should equal(Int.MaxValue, Int.MaxValue)
    }

    it("unapply"){
      val Z2(x,y) = Z2(3,5)
      x should be (3)
      y should be (5)
    }

    it("replaces example in Tropf, Herzog paper"){
      // Herzog example inverts x and y, with x getting higher sigfigs
      val rmin = Z2(5,3)
      val rmax = Z2(10,5)
      val p = Z2(4, 7)

      rmin.z should equal (27)
      rmax.z should equal (102)
      p.z should equal (58)

      val (litmax, bigmin) = Z2.zdivide(p.z, rmin.z, rmax.z)

      litmax should equal (55)
      bigmin should equal (74)
    }

    it("replicates the wikipedia example") {
      val rmin = Z2(2,2)
      val rmax = Z2(3,6)
      val p = Z2(5, 1)

      rmin.z should equal (12)
      rmax.z should equal (45)
      p.z should equal (19)

      val (litmax, bigmin) = Z2.zdivide(p.z, rmin.z, rmax.z)

      litmax should equal (15)
      bigmin should equal (36)
    }

    it("support maxRanges") {
      val ranges = Seq(
        ZRange(0L, 4611686018427387903L), // (sfc.index(-180, -90),      sfc.index(180, 90)),        // whole world
        ZRange(864691128455135232L, 4323455642275676160L), // (sfc.index(35, 65),         sfc.index(45, 75)),         // 10^2 degrees
        ZRange(4105065703422263800L, 4261005727442805282L), // (sfc.index(-90, -45),       sfc.index(90, 45)),         // half world
        ZRange(4069591195588206970L, 4261005727442805282L), // (sfc.index(35, 55),         sfc.index(45, 75)),         // 10x20 degrees
        ZRange(4105065703422263800L, 4202182393016524625L), // (sfc.index(35, 65),         sfc.index(37, 68)),         // 2x3 degrees
        ZRange(4105065703422263800L, 4203729178335734358L), // (sfc.index(35, 65),         sfc.index(40, 70)),         // 5^2 degrees
        ZRange(4097762467352558080L, 4097762468106131815L), // (sfc.index(39.999, 60.999), sfc.index(40.001, 61.001)), // small bounds
        ZRange(4117455696967246884L, 4117458209718964953L), // (sfc.index(51.0, 51.0),     sfc.index(51.1, 51.1)),     // small bounds
        ZRange(4117455696967246884L, 4117455697154258685L), // (sfc.index(51.0, 51.0),     sfc.index(51.001, 51.001)), // small bounds
        ZRange(4117455696967246884L, 4117455696967246886L) // (sfc.index(51.0, 51.0),     sfc.index(51.0000001, 51.0000001)) // 60 bits in common
      )

      ranges.foreach { r =>
        val ret = Z2.zranges(Array(r), maxRanges = Some(1000))
        ret.length should be >= 0
        ret.length should be <= 1000
      }
    }
  }
}
