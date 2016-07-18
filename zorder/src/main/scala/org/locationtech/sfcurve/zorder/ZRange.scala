/*
 * Copyright (c) 2013-2016 Commonwealth Computer Research, Inc.
 * Copyright (c) 2015 Azavea.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0 which
 * accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 */

package org.locationtech.sfcurve.zorder

/**
 * Represents a rectangle in defined by min and max as two opposing points
 * 
 * @param min: lower-left point 
 * @param max: upper-right point
 */
case class ZRange(min: Long, max: Long) {

  require(min <= max, s"Range bounds must be ordered, but $min > $max")

  def mid: Long = (max + min)  >>> 1 // overflow safe mean

  def length: Long = max - min + 1

  // contains in index space (e.g. the long value)
  def contains(bits: Long): Boolean = bits >= min && bits <= max

  // contains in index space (e.g. the long value)
  def contains(r: ZRange): Boolean =  contains(r.min) && contains(r.max)

  // overlaps in index space (e.g. the long value)
  def overlaps(r: ZRange): Boolean = contains(r.min) || contains(r.max)
}

object ZRange {
  def apply(min: Z2, max: Z2)(implicit d: DummyImplicit): ZRange = ZRange(min.z, max.z)
  def apply(min: Z3, max: Z3)(implicit d1: DummyImplicit, d2: DummyImplicit): ZRange = ZRange(min.z, max.z)
}
