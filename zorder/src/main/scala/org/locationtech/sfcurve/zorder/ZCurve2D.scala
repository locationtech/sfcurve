/***********************************************************************
 * Copyright (c) 2015 Azavea.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0 which
 * accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 ***********************************************************************/

package org.locationtech.sfcurve.zorder

import org.locationtech.sfcurve._

/** Represents a 2D Z order curve that we will use for benchmarking purposes in the early stages.
  *
  * @param    resolution     The number of cells in each dimension of the grid space that will be indexed.
  */
class ZCurve2D(resolution: Int) extends SpaceFillingCurve2D {
  // We are assuming Lat Lng extent for the whole world
  val xmin = -180.0
  val ymin = -90.0
  val xmax = 180.0
  val ymax = 90.0

  // Code taken from geotrellis.raster.RasterExtent
  val cellwidth = (xmax - xmin) / resolution
  val cellheight = (ymax - ymin) / resolution

  private final def mapToCol(x: Double) =
    ((x - xmin) / cellwidth).toInt

  private final def mapToRow(y: Double) =
    ((ymax - y) / cellheight).toInt

  private final def colToMap(col: Int) =
    math.max(math.min(col * cellwidth + xmin + (cellwidth / 2), xmax), xmin)

  private final def rowToMap(row: Int) =
    math.min(math.max(ymax - (row * cellheight) - (cellheight / 2), ymin), ymax)


  def toIndex(x: Double, y: Double): Long = {
    val col = mapToCol(x)
    val row = mapToRow(y)
    Z2(col, row).z
  }

  def toPoint(i: Long): (Double, Double) = {
    val (col, row) = new Z2(i).decode
    (colToMap(col), rowToMap(row))
  }

  def toRanges(xmin: Double, ymin: Double, xmax: Double, ymax: Double): Seq[(Long, Long)] = {
    val colMin = mapToCol(xmin)
    val rowMin = mapToRow(ymax)
    println("Input: " + xmin + " ymin: " + ymin + " xmax: " + xmax + " ymax: " + ymax)
    val min = Z2(rowMin, colMin)
    println("minmapToCol: " + colMin + " minmapToRow: " + rowMin + " z: " + min.z)
    val colMax = mapToCol(xmax)
    val rowMax = mapToRow(ymin)
    val max = Z2(rowMin, colMin)
    println("maxmapToCol: " + colMax + " maxmapToRow: " + rowMax + " z: " + max.z)

    Z2.zranges(min, max)
  }
}
