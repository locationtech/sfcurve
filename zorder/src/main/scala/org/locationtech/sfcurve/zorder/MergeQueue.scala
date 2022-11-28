/***********************************************************************
 * Copyright (c) 2015 Azavea.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0 which
 * accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 ***********************************************************************/

package org.locationtech.sfcurve.zorder

import org.locationtech.sfcurve.IndexRange

class MergeQueue(initialSize: Int = 1) {
  private var array = if(initialSize <= 1) { Array.ofDim[IndexRange](1) } else { Array.ofDim[IndexRange](initialSize) }
  private var _size = 0
 
  def size = _size
 
  private def removeElement(i: Int): Unit = {
    if(i < _size - 1) {
      val result = array.clone
      System.arraycopy(array, i + 1, result, i, _size - i - 1)
      array = result
    }
    _size = _size - 1
  }
 
  private def insertElement(range: IndexRange, i: Int): Unit = {
    ensureSize(_size + 1)
    if(i == _size) {
      array(i) = range
    } else {
      val result = array.clone
      System.arraycopy(array, 0, result, 0, i)
      System.arraycopy(array, i, result, i + 1, _size - i)
      result(i) = range
      array = result
    }
    _size += 1
  }
 
 
  /** Ensure that the internal array has at least `n` cells. */
  protected def ensureSize(n: Int): Unit = {
    // Use a Long to prevent overflows
    val arrayLength: Long = array.length
    if (n > arrayLength - 1) {
      var newSize: Long = arrayLength * 2
      while (n > newSize) {
        newSize = newSize * 2
      }
      // Clamp newSize to Int.MaxValue
      if (newSize > Int.MaxValue) newSize = Int.MaxValue
 
      val newArray: Array[IndexRange] = new Array(newSize.toInt)
      System.arraycopy(array, 0, newArray, 0, _size)
      array = newArray
    }
  }

  val ordering = IndexRange.IndexRangeIsOrdered
 
  /** Inserts a single range into the priority queue.
   *
   *  @param  range        the element to insert.
   */
  final def +=(range: IndexRange): Unit = {
    val res = if(_size == 0) { -1 } else { java.util.Arrays.binarySearch(array, 0, _size, range, ordering) }
    if(res < 0) {
      val i = -(res + 1)
      var (thisStart, thisEnd, contained) = range.tuple
      var removeLeft = false
 
      var removeRight = false
      var rightRemainder: Option[IndexRange] = None
 
      // Look at the left range
      if(i != 0) {
        val (prevStart, prevEnd, b) = array(i - 1).tuple
        if(prevStart == thisStart) {
          contained = contained && b
          removeLeft = true
        }
        if (prevEnd + 1 >= thisStart) {
          contained = contained && b
          removeLeft = true
          thisStart = prevStart
          if(prevEnd > thisEnd) {
            thisEnd = prevEnd
          }
        }
      }
 
      // Look at the right range
      if(i < _size  && _size > 0) {
        val (nextStart, nextEnd, b) = array(i).tuple
        if(thisStart == nextStart) {
          removeRight = true
          thisEnd = nextEnd
          contained = contained && b
        } else {
          if(thisEnd + 1 >= nextStart) {
            removeRight = true
            contained = contained && b
            if(nextEnd - 1 >= thisEnd) {
              thisEnd = nextEnd
            } else if (nextEnd < thisEnd - 1) {
              rightRemainder = Some(IndexRange(nextEnd + 1, thisEnd, contained && b))
              thisEnd = nextEnd
            }
          }
        }
      }
 
      if(removeRight) { 
        if(!removeLeft) {
          array(i) = IndexRange(thisStart, thisEnd, contained)
        } else {
          array(i-1) = IndexRange(thisStart, thisEnd, contained)
          removeElement(i)
        }
      } else if(removeLeft) {
        array(i-1) = IndexRange(thisStart, thisEnd, contained)
      } else {
        insertElement(range, i)
      }
      
      rightRemainder match {
        case Some(r) => this += r
        case None =>
      }
    }
  }
 
  def toSeq: Seq[IndexRange] =
    Seq.tabulate[IndexRange](size)(array.apply)
}
