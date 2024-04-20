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
package de.flapdoodle.kfx.controls.grapheditor.background

import javafx.geometry.Bounds

data class GridBounds(
  val minX: Double,
  val minY: Double,
  val maxX: Double,
  val maxY: Double,
  val spacing: Double
) {

  fun forEachX(action: (Double) -> Unit) {
    val steps = ((maxX - minX) / spacing).toInt()
    for (s in 0..steps) {
      action(s * spacing + minX)
    }
  }

  fun forEachY(action: (Double) -> Unit) {
    val steps = ((maxY - minY) / spacing).toInt()
    for (s in 0..steps) {
      action(s * spacing + minY)
    }
  }

  companion object {
    fun of(bounds: Bounds, spacing: Double): GridBounds {
      val x = lastValueBefore(bounds.minX, spacing)
      val y = lastValueBefore(bounds.minY, spacing)
      val xe = firstValueAfter(bounds.maxX, spacing)
      val ye = firstValueAfter(bounds.maxY, spacing)
      return GridBounds(x, y, xe, ye, spacing)
    }

    private fun lastValueBefore(value: Double, step: Double): Double {
      val offset = if (value > 0.0) 0 else 1
      return ((value / step).toInt() - offset) * step
    }

    private fun firstValueAfter(value: Double, step: Double): Double {
      val offset = if (value < 0.0) 0 else 1
      return ((value / step).toInt() + offset) * step
    }
  }
}