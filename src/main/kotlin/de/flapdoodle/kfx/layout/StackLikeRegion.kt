/*
 * Copyright (C) 2022
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.kfx.layout

import javafx.collections.ObservableList
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.layout.Region
import kotlin.math.max

open class StackLikeRegion : Region() {
  private val debug = false
  
  final override fun computeMinHeight(width: Double): Double {
    val height = children.map { it.minHeight(width) }.fold(0.0) { l, r -> max(l, r) }
    if (debug) println("computeMinHeight: ${insets.top} + $height + ${insets.bottom}")
    return insets.top + height + insets.bottom
  }

  final override fun computeMinWidth(height: Double): Double {
    val width = children.map { it.minWidth(height) }.fold(0.0) { l, r -> max(l, r) }
    if (debug) println("computeMinWidth: ${insets.left} + $width + ${insets.right}")
    return insets.left + width + insets.right
  }

  final override fun computePrefHeight(width: Double): Double {
    val height = children.map { it.prefHeight(width) }.fold(0.0) { l, r -> max(l, r) }
    if (debug) println("computePrefHeight: ${insets.top} + $height + ${insets.bottom}")
    return insets.top + height + insets.bottom
  }

  final override fun computePrefWidth(height: Double): Double {
    val width = children.map { it.prefWidth(height) }.fold(0.0) { l, r -> max(l, r) }
    if (debug) println("computePrefWidth: ${insets.left} + $width + ${insets.right}")
    return insets.left + width + insets.right
  }

  override fun layoutChildren() {
    children.forEach {
      if (it.isManaged) {
        layoutInArea(it, insets.left, insets.top, width - insets.left - insets.right, height - insets.top - insets.bottom, -1.0, HPos.LEFT, VPos.TOP)
      }
    }
  }

  class PaneLike : StackLikeRegion() {
    public override fun getChildren(): ObservableList<Node> {
      return super.getChildren()
    }
  }
}