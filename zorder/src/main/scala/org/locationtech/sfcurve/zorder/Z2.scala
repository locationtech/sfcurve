/***********************************************************************
 * Copyright (c) 2015 Azavea.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0 which
 * accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 ***********************************************************************/

package org.locationtech.sfcurve.zorder

import org.locationtech.sfcurve.IndexRange
import org.locationtech.sfcurve.zorder.Z3.ZPrefix

class Z2(val z: Long) extends AnyVal {
  import Z2._

  def < (other: Z2) = z < other.z
  def > (other: Z2) = z > other.z

  def + (offset: Long) = new Z2(z + offset)
  def - (offset: Long) = new Z2(z - offset)

  def == (other: Z2) = other.z == z

  def decode: (Int, Int) = (combine(z), combine(z>>1))

  def dim(i: Int) = Z2.combine(z >> i)

  def d0 = dim(0)
  def d1 = dim(1)

  def mid(p: Z2): Z2 = {
    val (x, y) = decode
    val (px, py) = p.decode
    Z2((x + px) >>> 1, (y + py) >>> 1) // overflow safe mean
  }

  def bitsToString = f"(${z.toBinaryString}%16s)(${dim(0).toBinaryString}%8s,${dim(1).toBinaryString}%8s)"
  override def toString = f"$z $decode"
}

object Z2 {  
  final val MAX_BITS = 31
  final val MAX_MASK = 0x7fffffff // ignore the sign bit, using it breaks < relationship
  final val MAX_DIM = 2
  final val TOTAL_BITS = MAX_BITS * MAX_DIM

  /** insert 0 between every bit in value. Only first 31 bits can be considered. */
  def split(value: Long): Long = {
    var x: Long = value & MAX_MASK  
    x = (x ^ (x << 32)) & 0x00000000ffffffffL
    x = (x ^ (x << 16)) & 0x0000ffff0000ffffL
    x = (x ^ (x <<  8)) & 0x00ff00ff00ff00ffL // 11111111000000001111111100000000..
    x = (x ^ (x <<  4)) & 0x0f0f0f0f0f0f0f0fL // 1111000011110000
    x = (x ^ (x <<  2)) & 0x3333333333333333L // 11001100..
    x = (x ^ (x <<  1)) & 0x5555555555555555L // 1010...
    x
  }

  /** combine every other bit to form a value. Maximum value is 31 bits. */
  def combine(z: Long): Int = {
    var x = z & 0x5555555555555555L
    x = (x ^ (x >>  1)) & 0x3333333333333333L
    x = (x ^ (x >>  2)) & 0x0f0f0f0f0f0f0f0fL
    x = (x ^ (x >>  4)) & 0x00ff00ff00ff00ffL
    x = (x ^ (x >>  8)) & 0x0000ffff0000ffffL
    x = (x ^ (x >> 16)) & 0x00000000ffffffffL
    x.toInt
  }

  def apply(zvalue: Long): Z2 = new Z2(zvalue)

  /**
   * Bits of x and y will be encoded as ....y1x1y0x0
   */
  def apply(x: Int, y:  Int): Z2 = 
    new Z2(split(x) | split(y) << 1)  

  def unapply(z: Z2): Option[(Int, Int)] = 
    Some(z.decode)
  
  def zdivide(p: Z2, rmin: Z2, rmax: Z2): (Z2, Z2) = {
    val (litmax,bigmin) = zdiv(load, MAX_DIM)(p.z, rmin.z, rmax.z)
    (new Z2(litmax), new Z2(bigmin))
  }
  
  /** Loads either 1000... or 0111... into starting at given bit index of a given dimention */
  def load(target: Long, p: Long, bits: Int, dim: Int): Long = {    
    val mask = ~(Z2.split(MAX_MASK >> (MAX_BITS-bits)) << dim)
    val wiped = target & mask
    wiped | (split(p) << dim)
  }

  /**
    * Calculates the longest common binary prefix between two z longs
    *
    * @return (common prefix, number of bits in common)
    */
  def longestCommonPrefix(lower: Long, upper: Long): ZPrefix = {
    var bitShift = TOTAL_BITS - MAX_DIM
    while ((lower >>> bitShift) == (upper >>> bitShift) && bitShift > -1) {
      bitShift -= MAX_DIM
    }
    bitShift += MAX_DIM // increment back to the last valid value
    ZPrefix(lower & (Long.MaxValue << bitShift), 64 - bitShift)
  }

  // base our recursion on the depth of the tree that we get 'for free' from the common prefix
  // these numbers generally result in 500-3000 ranges being returned
  def getMaxRecurse(commonBits: Int): Int = if (commonBits < 30) 10 else if (commonBits < 40) 9 else 7

  /**
    * Recurse down the quad-tree and report all z-ranges which are contained
    * in the rectangle defined by the min and max points
    *
    * @param min lower bound
    * @param max upper bound
    * @param precision bit precision of the z-values. can be used to stop searching after
    *                  considering a certain number of bits.
    * @return
    */
  def zranges(min: Z2, max: Z2, precision: Int = 64, maxRecursion: Option[Int] = None): Seq[IndexRange] = {
    val ZPrefix(commonPrefix, commonBits) = longestCommonPrefix(min.z, max.z)

    val maxRecurse = maxRecursion.getOrElse(getMaxRecurse(commonBits))

    val searchRange = Z2Range(min, max)
    val mq = new MergeQueue // stores our results

    def checkQuad(prefix: Long, offset: Int, quad: Long, level: Int): Unit = {
      val min: Long = prefix | (quad << offset) // QR + 000..
      val max: Long = min | (1L << offset) - 1  // QR + 111..
      val quadRange = Z2Range(new Z2(min), new Z2(max))

      if (searchRange.containsInUserSpace(quadRange) || offset < 64 - precision) {
        // whole range matches, happy day
        mq += IndexRange(quadRange.min.z, quadRange.max.z, contained = true)
      } else if (searchRange.overlapsInUserSpace(quadRange)) {
        if (level < maxRecurse && offset > 0) {
          // some portion of this range are excluded
          // let our children work on each subrange
          val nextOffset = offset - MAX_DIM
          val nextLevel = level + 1
          checkQuad(min, nextOffset, 0, nextLevel)
          checkQuad(min, nextOffset, 1, nextLevel)
          checkQuad(min, nextOffset, 2, nextLevel)
          checkQuad(min, nextOffset, 3, nextLevel)
        } else {
          // bottom out - add the entire range so we don't miss anything
          mq += IndexRange(quadRange.min.z, quadRange.max.z, contained = false)
        }
      }
    }

    // kick off recursion over the narrowed space
    checkQuad(commonPrefix, 64 - commonBits, 0, 0)

    // return our aggregated results
    mq.toSeq
  }
}
