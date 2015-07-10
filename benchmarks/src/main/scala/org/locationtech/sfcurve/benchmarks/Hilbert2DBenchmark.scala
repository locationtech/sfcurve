/***********************************************************************
 * Copyright (c) 2015 Azavea.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0 which
 * accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 ***********************************************************************/

package org.locationtech.sfcurve.benchmarks

import org.locationtech.sfcurve.hilbert._
import com.google.caliper.Param


object HilbertBenchmark extends BenchmarkRunner(classOf[HilbertBenchmark])

class HilbertBenchmark extends CurveBenchmark {


  def timeHilbertBadCase(reps: Int) = run(reps)(HilbertBadCase)
  def HilbertBadCase = {

      var i = 2 //resolution bits
      while (i < 20){
          val sfc = new HilbertCurve2D(i)
          val range = sfc.toRanges(-178.123456, -86.398493, 179.3211113, 87.393483)
          i += 1
      }

  }

  def timeHilbertCityCase(reps: Int) = run(reps)(HilbertCityCase)
  def HilbertCityCase = {
      var i = 10
      var lx = -180
      var ly = 89.68103
      var ux = -179.597625
      var uy = 90
      var yrun = 0
      var xrun = 0

      while (i < 24){
          val sfc = new HilbertCurve2D(i)
          while((yrun + ly) > -90){
              xrun = 0
              while ((xrun + ux) < 180){
                  val range = sfc.toRanges(lx+xrun, ly+yrun, ux+xrun, uy+yrun)
                  xrun += 5
              }
              yrun -= 5
          }
          i += 1
      }
  }

  def timeHilbertStateCase(reps: Int) = run(reps)(HilbertStateCase)
  def HilbertStateCase = {
      var i = 6
      var lx = -180
      var ly = 86.022914
      var ux = -173.078613
      var uy = 90
      var yrun = 0
      var xrun = 0

      while (i < 24){
          val sfc = new HilbertCurve2D(i)
          while ((yrun + ly) > -90){
              xrun = 0
              while((xrun + ux) < 180){
                  val range = sfc.toRanges(lx+xrun, ly+yrun, ux+xrun, uy+yrun)
                  xrun += 5
              }
              yrun -= 5
          }

          i += 1
      }
  }

  def timeHilbertCountryCase(reps: Int) = run(reps)(HilbertCountryCase)
  def HilbertCountryCase = {
      var i = 6
      var lx = -180
      var ly = 82.689749
      var ux = -171.408692
      var uy = 90
      var yrun = 0
      var xrun = 0

      while (i < 24){
          val sfc = new HilbertCurve2D(i)
          while ((yrun + ly) > -90){
              xrun = 0
              while((xrun + ux) < 180){
                  val range = sfc.toRanges(lx+xrun, ly+yrun, ux+xrun, uy+yrun)
                  xrun += 5
              }
              yrun -= 5
          }

          i += 1
      }
  }
  //Are we doing the error on tiling vs. coordinates?

}
