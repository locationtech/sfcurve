/***********************************************************************
 * Copyright (c) 2015 Azavea.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0 which
 * accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 ***********************************************************************/

package org.locationtech.sfcurve.benchmarks

import org.locationtech.sfcurve.zorder._
import com.google.caliper.Param

object SFCurveBenchmarks extends BenchmarkRunner(classOf[SFCurveBenchmarks])
class SFCurveBenchmarks extends CurveBenchmark {

  val pts = (0 until 300).toArray

  /*def timeZ2IndexCreate(reps: Int) = run(reps)(z2IndexCreation)
  def z2IndexCreation = {

    var res = 2
    while(res < 24){
        new ZCurve2D(res)
        res += 1
    }
}*/

  def timeZ2BadCase(reps: Int) = run(reps)(Z2BadCase)
  def Z2BadCase = {
      var t1 = Z2(0,90)
      var t2 = Z2(180,0)
      println("t1: " + t1.z + " t2: " + t2.z)
      var i = 2 //resolution bits
      while (i < 20){
          val sfc = new ZCurve2D(i)
          val range = sfc.toRanges(-178.123456, -86.398493, 179.3211113, 87.393483)
          i += 1
      }

  }

  def timeZ2CityCase(reps: Int) = run(reps)(Z2CityCase)
  def Z2CityCase = {
      var i = 10
      var lx = -180
      var ly = 89.68103
      var ux = -179.597625
      var uy = 90
      var yrun = 0
      var xrun = 0

      while (i < 24){
          val sfc = new ZCurve2D(i)
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

  def timeZ2StateCase(reps: Int) = run(reps)(Z2StateCase)
  def Z2StateCase = {
      var i = 6
      var lx = -180
      var ly = 86.022914
      var ux = -173.078613
      var uy = 90
      var yrun = 0
      var xrun = 0

      while (i < 24){
          val sfc = new ZCurve2D(i)
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

  def timeZ2CountryCase(reps: Int) = run(reps)(Z2CountryCase)
  def Z2CountryCase = {
      var i = 6
      var lx = -180
      var ly = 82.689749
      var ux = -171.408692
      var uy = 90
      var yrun = 0
      var xrun = 0

      while (i < 24){
          val sfc = new ZCurve2D(i)
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

  /*def timeZ3IndexCreate(reps: Int) = run(reps)(z3IndexCreation)
  def z3IndexCreation = {

    var x = 100
    var y = 100
    var z = 100

    while(x < 200) {
      while(y < 200) {
        while(z < 200) {
          Z3(pts(x), pts(y), pts(z))
          z += 1
        }
        y += 1
      }
      x += 1
    }
  }

  def timeZ3ZRanges(reps: Int) = run(reps)(z3ZRangesCreation)
  def z3ZRangesCreation = {
    var x = 100
    var y = 100
    var z = 100

    while(x < 100){
        while(y < 100){
            while(z < 100){
                var z31 = Z3(x-100, y-100, z-100)
                var z32 = Z3(x, y, z)
                Z3.zranges(z31, z32)
                z += 1
            }
            y += 1
        }
        x += 1
    }
}*/

}
