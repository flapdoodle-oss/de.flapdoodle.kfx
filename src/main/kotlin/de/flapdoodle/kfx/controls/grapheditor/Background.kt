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
package de.flapdoodle.kfx.controls.grapheditor

import de.flapdoodle.kfx.controls.grapheditor.background.GridBounds
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.shape.LineTo
import javafx.scene.shape.MoveTo
import javafx.scene.shape.Path

class Background : Region() {
  private val HALF_PIXEL_OFFSET = -0.5;
  private val color: Color = Color.rgb(220, 220, 220)
  private val grid = Path().apply {
    stroke = color
  }

  //  private val scrollXBounds = SimpleObjectProperty<ScrollBounds>(ScrollBounds(0.0,10.0,2.0))
//  private val scrollYBounds = SimpleObjectProperty<ScrollBounds>(ScrollBounds(0.0,10.0,2.0))
  private val bounds = SimpleObjectProperty<Bounds>(BoundingBox(-50.0, -50.0, 100.0, 100.0))

  init {
    isManaged = false
    isMouseTransparent = true

    children.add(grid)
    bounds.subscribe(::redraw)
  }

  private fun redraw(bounds: Bounds) {
    val spacing: Double = 10.0
    val gridBounds = GridBounds.of(bounds, spacing)

    grid.elements.clear()

    gridBounds.forEachX { xg ->
      val x: Double = xg + HALF_PIXEL_OFFSET
      grid.elements.add(MoveTo(x, gridBounds.minY))
      grid.elements.add(LineTo(x, gridBounds.maxY))
    }

    gridBounds.forEachY { yg ->
      val y: Double = yg + HALF_PIXEL_OFFSET
      grid.elements.add(MoveTo(bounds.minX, y))
      grid.elements.add(LineTo(bounds.maxX, y))
    }
  }

  fun area(): ObjectProperty<Bounds> = bounds
}