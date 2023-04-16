package de.flapdoodle.kfx.extensions

import javafx.geometry.Bounds

fun Bounds.multiply(factor: Double): Bounds {
  return BoundingBoxes.multiply(this, factor)
}