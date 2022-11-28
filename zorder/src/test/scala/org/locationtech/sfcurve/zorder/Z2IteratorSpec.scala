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

class Z2IteratorSpec extends AnyFunSpec with Matchers {
  describe("Z2IteratorRange") {

    it("iterates"){
      val min = Z2(5,3)
      val max = Z2(10,5)
      val range = ZRange(min, max)
      val it = new ZdivideIterator(min, max)

      it foreach { z2: Z2 =>
        range.contains(z2.z)
      }
    }
  }

  describe("ZCurve2D"){
    it("creates a bounding box"){
      val sfc = new ZCurve2D(2)
      val range = sfc.toRanges(-178.123456, -86.398493, 179.3211113, 87.393483)

      range should have length 1
    }
  }
}
/**
 * Base iterator that is able to seek forward to some index.
 * This iterator will eventually hit z values that are outside of range defined by min and max
 */
class Z2Iterator(min: Z2, max: Z2) extends Iterator[Z2] {
  private var cur = min

  def hasNext: Boolean = cur.z <= max.z

  def next: Z2 = {
    val ret = cur
    cur += 1
    ret
  }

  def seek(min: Z2, max: Z2) = {
    cur = min
  }
}

/**
 * Iterator that uses zdivide to decide when to seek forward.
 * As we encounter z values outside of our query range we decide how many misses we can sustain
 * before using zdivide to seek forward. The assumption that there is some number of calls to .next()
 * that will equal a cost of .seek().
 *
 * This is a mock class.
 */
case class ZdivideIterator(min: Z2, max: Z2) extends Z2Iterator(min, max)  {
  val MAX_MISSES = 10
  val range = ZRange(min, max)
  var haveNext = false
  var _next: Z2 = new Z2(0)

  advance

  override def hasNext: Boolean = haveNext

  override def next: Z2 = {
    // it's safe to report cur, because we've advanced to it and hasNext has been called.
    val ret = _next
    advance
    ret
  }

  /**
   * Two possible post-conditions:
   * 1. cur is set to a valid object
   * 2. cur is set to null and source.hasNext == false
   */
  def advance: Unit = {
    var misses = 0
    while (misses < MAX_MISSES && super.hasNext) {
      _next = super.next
      if (range.contains(_next.z)) {
        haveNext = true
        return
      } else {
        misses + 1
      }
    }

    if (_next < max) {
      val (litmax, bigmin) = Z2.zdivide(_next.z, min.z, max.z)
      _next = Z2(bigmin)
      haveNext = true
    } else {
      haveNext = false
    }
  }
}
