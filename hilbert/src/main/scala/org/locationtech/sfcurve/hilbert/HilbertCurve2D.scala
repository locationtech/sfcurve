/***********************************************************************
 * Copyright (c) 2015 Azavea.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0 which
 * accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 ***********************************************************************/

package org.locationtech.sfcurve.hilbert

import java.io.Serializable

import com.google.common.base.{Function, Functions}
import com.google.common.collect.ImmutableList
import com.google.uzaygezen.core._
import com.google.uzaygezen.core.ranges._
import org.locationtech.sfcurve._

class HilbertCurve2DProvider extends SpaceFillingCurveProvider {
  override def canProvide(name: String): Boolean = "hilbert" == name

  override def build2DSFC(args: Map[String, Serializable]): SpaceFillingCurve2D =
    new HilbertCurve2D(args(HilbertCurve2DProvider.RESOLUTION_PARAM).asInstanceOf[Int])
}

object HilbertCurve2DProvider {
  val RESOLUTION_PARAM = "hilbert.resolution"
}

class HilbertCurve2D(resolution: Int) extends SpaceFillingCurve2D {
  val precision = math.pow(2, resolution).toLong
  val chc = new CompactHilbertCurve(Array(resolution, resolution))

  final def getNormalizedLongitude(x: Double): Long = 
    ((x + 180) * (precision - 1) / 360d).toLong
//    (x * (precision - 1) / 360d).toLong

  final def getNormalizedLatitude(y: Double): Long =
    ((y + 90) * (precision - 1) / 180d).toLong
//    (y * (precision - 1) / 180d).toLong

  final def setNormalizedLatitude(latNormal: Long) = {
    if(!(latNormal >= 0 && latNormal <= precision))
      throw new NumberFormatException("Normalized latitude must be greater than 0 and less than the maximum precision")

    latNormal * 180d / (precision - 1)
  }

  final def setNormalizedLongitude(lonNormal: Long) = {
    if(!(lonNormal >= 0 && lonNormal <= precision))
      throw new NumberFormatException("Normalized longitude must be greater than 0 and less than the maximum precision")

    lonNormal * 360d / (precision - 1)
  }


  def toIndex(x: Double, y: Double): Long = {
    val normX = getNormalizedLongitude(x)
    val normY = getNormalizedLatitude(y)
    val p = 
      Array[BitVector](
        BitVectorFactories.OPTIMAL(resolution),
        BitVectorFactories.OPTIMAL(resolution)
      )

    p(0).copyFrom(normX)
    p(1).copyFrom(normY)

    val hilbert = BitVectorFactories.OPTIMAL.apply(resolution * 2)

    chc.index(p,0,hilbert)
    hilbert.toLong
  }

  def toPoint(i: Long): (Double, Double) = {
    val h = BitVectorFactories.OPTIMAL.apply(resolution*2)
    h.copyFrom(i)
    val p = 
      Array[BitVector](
        BitVectorFactories.OPTIMAL(resolution),
        BitVectorFactories.OPTIMAL(resolution)
      )

    chc.indexInverse(h,p)

    val x = setNormalizedLongitude(p(0).toLong) - 180
    val y = setNormalizedLatitude(p(1).toLong) - 90
    (x, y)
  }

  def toRanges(xmin: Double, ymin: Double, xmax: Double, ymax: Double, hints: Option[RangeComputeHints] = None): Seq[IndexRange] = {
    val chc = new CompactHilbertCurve(Array[Int](resolution, resolution))
    val region = new java.util.ArrayList[LongRange]()

    val minNormalizedLongitude = getNormalizedLongitude(xmin)
    val minNormalizedLatitude  = getNormalizedLatitude(ymin) 

    val maxNormalizedLongitude = getNormalizedLongitude(xmax)
    val maxNormalizedLatitude  = getNormalizedLatitude(ymax) 

    region.add(LongRange.of(minNormalizedLongitude,maxNormalizedLongitude))
    region.add(LongRange.of(minNormalizedLatitude,maxNormalizedLatitude))

    val zero = new LongContent(0L)
    val LongRangeIDFunction: Function[LongRange, LongRange] = Functions.identity()

    val inspector =
      SimpleRegionInspector.create(
        ImmutableList.of(region),
        new LongContent(1L),
        LongRangeIDFunction,
        LongRangeHome.INSTANCE,
        zero
      )

    val combiner =
      new PlainFilterCombiner[LongRange, java.lang.Long, LongContent, LongRange](LongRange.of(0, 1))

    val queryBuilder = BacktrackingQueryBuilder.create(inspector, combiner, Int.MaxValue, true, LongRangeHome.INSTANCE, zero)

    chc.accept(new ZoomingSpaceVisitorAdapter(chc, queryBuilder))

    val query = queryBuilder.get()

    val ranges = query.getFilteredIndexRanges

    //result
    var result = List[IndexRange]()
    val itr = ranges.iterator

    while(itr.hasNext) {
      val l = itr.next()
      val range = l.getIndexRange
      val start = range.getStart.asInstanceOf[Long]
      val end   = range.getEnd.asInstanceOf[Long]
      val contained = l.isPotentialOverSelectivity
      result = IndexRange(start, end, contained) :: result
    }
    result
  }
}
