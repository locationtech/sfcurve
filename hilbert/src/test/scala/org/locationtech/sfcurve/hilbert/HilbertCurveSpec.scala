/***********************************************************************
 * Copyright (c) 2015 Azavea.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0 which
 * accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 ***********************************************************************/

package org.locationtech.sfcurve.hilbert

import org.locationtech.sfcurve.SpaceFillingCurves
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class HilbertCurveSpec extends AnyFunSpec with Matchers  {

  val EPSILON: Double = 1E-3

  describe("SPI access") {
    it("resolves a HilbertCurve2D") {
      val sfc = SpaceFillingCurves("hilbert", Map(HilbertCurve2DProvider.RESOLUTION_PARAM -> Int.box(10)))
      sfc.isInstanceOf[HilbertCurve2D] should equal(true)
    }
  }
  describe("A HilbertCurve implementation using UG lib") {

    it("translates (Double,Double) to Long and Long to (Double, Double)"){
      val resolution = 16
      val gridCells = math.pow(2, resolution)

      val sfc = new HilbertCurve2D(resolution)
      val index: Long = sfc.toIndex(0.0, 0.0)

      val xEpsilon = 360.0 / gridCells
      val yEpsilon = 180.0 / gridCells

      val point = sfc.toPoint(index)

      point._1 should be (-(xEpsilon / 2.0) +- 0.000001)
      point._2 should be (-(yEpsilon / 2.0) +- 0.000001)      
    }

    it("implements a range query"){

      val sfc = new HilbertCurve2D(3)
      val range = sfc.toRanges(-178.123456, -86.398493, 179.3211113, 87.393483)

      range should have length 3

      // the last range is not wholly contained within the query region
      val (_, _, lastcontains) = range(2).tuple
      lastcontains should be(false)
    }

    it("Takes a Long value to a Point (Double, Double)"){

      val value = 0L
      val sfc = new HilbertCurve2D(8)
      val point: (Double, Double) = sfc.toPoint(value)
      print(point)     

    }

    it("Takes a Point (Double, Double) to a Long value"){

      val sfc = new HilbertCurve2D(8)
      val value: Long = sfc.toIndex(0.0,0.0)
      print(value)     

    }

  }
}
