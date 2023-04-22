package de.flapdoodle.kfx.extensions

import javafx.geometry.Point2D

object Point2DMath {
  fun angle(p1: Point2D, p2: Point2D): Double {
    val xDiff = p2.x - p1.x
    val yDiff = p2.y - p1.y
    return Math.toDegrees(Math.atan2(yDiff, xDiff))
  }
}