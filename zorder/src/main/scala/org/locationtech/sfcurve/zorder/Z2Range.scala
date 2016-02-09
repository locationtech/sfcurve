/***********************************************************************
 * Copyright (c) 2015 Azavea.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0 which
 * accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 ***********************************************************************/

package org.locationtech.sfcurve.zorder

/**
 * Represents a rectangle in defined by min and max as two opposing points
 * 
 * @param min: lower-left point 
 * @param max: upper-right point
 */
case class Z2Range(min: Z2, max: Z2){  
  require(min.z < max.z || min.z == max.z, s"NOT: ${min.z} < ${max.z}")  
  def mid = min mid max

  def length = max.z - min.z
  
  def contains(z: Z2) = {
    val (x, y) = z.decode
    x >= min.dim(0) &&
    x <= max.dim(0) &&
    y >= min.dim(1) &&
    y <= max.dim(1)  
  }

  def contains(r: Z2Range): Boolean = 
    contains(r.min) && contains(r.max)

  def overlaps(r: Z2Range): Boolean = {
    def _overlaps(a1:Int, a2:Int, b1:Int, b2:Int) = math.max(a1,b1) <= math.min(a2,b2)
    _overlaps(min.dim(0), max.dim(0), r.min.dim(0), r.max.dim(0)) &&
    _overlaps(min.dim(1), max.dim(1), r.min.dim(1), r.max.dim(1))
  }

  // contains in user space - each dimension is contained
  def containsInUserSpace(bits: Z2) = {
    val (x, y) = bits.decode
    x >= min.d0 && x <= max.d0 && y >= min.d1 && y <= max.d1
  }

  // contains in user space - each dimension is contained
  def containsInUserSpace(r: Z2Range): Boolean = containsInUserSpace(r.min) && containsInUserSpace(r.max)

  // overlap in user space - if any dimension overlaps
  def overlapsInUserSpace(r: Z2Range): Boolean =
    overlaps(min.d0, max.d0, r.min.d0, r.max.d0) &&
      overlaps(min.d1, max.d1, r.min.d1, r.max.d1)

  private def overlaps(a1: Int, a2: Int, b1: Int, b2: Int) = math.max(a1, b1) <= math.min(a2, b2)


  /** 
   * Cuts Z-Range in two, can be used to perform augmented binary search
   * @param xd: division point
   * @param inRange: is xd in query range
   */
  def cut(xd: Z2, inRange: Boolean): List[Z2Range] = {    
    if (min.z == max.z)
      Nil
    else if (inRange) {
      if (xd.z == min.z)      // degenerate case, two nodes min has already been counted
        Z2Range(max, max) :: Nil
      else if (xd.z == max.z) // degenerate case, two nodes max has already been counted
        Z2Range(min, min) :: Nil
      else
        Z2Range(min, xd-1) :: Z2Range(xd+1, max) :: Nil
    } else {      
      val (litmax, bigmin) = Z2.zdivide(xd, min, max)
      Z2Range(min, litmax) :: Z2Range(bigmin, max) :: Nil
    }
  }
}
