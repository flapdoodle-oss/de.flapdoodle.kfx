package de.flapdoodle.kfx.layout

import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.layout.Region
import kotlin.math.max

open class StackLikeRegion : Region() {
  final override fun computeMinHeight(width: Double): Double {
    val height = children.map { it.minHeight(width) }.fold(0.0) { l, r -> max(l, r) }
    return insets.top + height + insets.bottom
  }

  final override fun computeMinWidth(height: Double): Double {
    val width = children.map { it.minWidth(height) }.fold(0.0) { l, r -> max(l, r) }
    return insets.left + width + insets.right
  }

  final override fun computePrefHeight(width: Double): Double {
    val height = children.map { it.prefHeight(width) }.fold(0.0) { l, r -> max(l, r) }
    return insets.top + height + insets.bottom
  }

  final override fun computePrefWidth(height: Double): Double {
    val width = children.map { it.prefWidth(height) }.fold(0.0) { l, r -> max(l, r) }
    return insets.left + width + insets.right
  }

  final override fun layoutChildren() {
    children.forEach {
      if (it.isManaged) {
        layoutInArea(it, insets.left, insets.top, width - insets.left - insets.right, height - insets.top - insets.bottom, -1.0, HPos.LEFT, VPos.TOP)
      }
    }
  }
}