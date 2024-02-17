package de.flapdoodle.kfx.extensions

import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import javafx.geometry.Insets

fun Bounds.multiply(factor: Double): Bounds {
  return BoundingBoxes.multiply(this, factor)
}

operator fun Bounds.minus(insets: Insets?): Bounds {
  if (insets != null) {
    return BoundingBox(
      minX + insets.left,
      minY + insets.top,
      width - insets.right - insets.left,
      height - insets.top - insets.bottom
    )
  }
  return this
}
