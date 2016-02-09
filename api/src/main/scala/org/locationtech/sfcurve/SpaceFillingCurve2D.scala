/***********************************************************************
 * Copyright (c) 2015 Azavea.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0 which
 * accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 ***********************************************************************/

package org.locationtech.sfcurve

class RangeComputeHints extends java.util.HashMap[String, AnyRef]

trait IndexRange {
  def lower: Long
  def upper: Long
  def contained: Boolean
  def tuple = (lower, upper, contained)
}

case class CoveredRange(lower: Long, upper: Long) extends IndexRange {
  val contained = true
}

case class OverlappingRange(lower: Long, upper: Long) extends IndexRange {
  val contained = false
}

object IndexRange {
  trait IndexRangeOrdering extends Ordering[IndexRange] {
    override def compare(x: IndexRange, y: IndexRange): Int = {
      val c1 = x.lower.compareTo(y.lower)
      if(c1 != 0) return c1
      val c2 = x.upper.compareTo(y.upper)
      if(c2 != 0) return c2
      0
    }
  }

  def apply(l: Long, u: Long, contained: Boolean): IndexRange =
    if(contained) CoveredRange(l, u)
    else          OverlappingRange(l, u)

  implicit object IndexRangeIsOrdered extends IndexRangeOrdering
}

trait SpaceFillingCurve2D {
  def toIndex(x: Double, y: Double): Long
  def toPoint(i: Long): (Double, Double)
  def toRanges(xmin: Double, ymin: Double, xmax: Double, ymax: Double, hints: Option[RangeComputeHints] = None): Seq[IndexRange]
}
