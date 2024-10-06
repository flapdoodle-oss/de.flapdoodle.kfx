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
package de.flapdoodle.kfx.controls.charts.parts

import de.flapdoodle.kfx.bindings.ObjectBindings
import de.flapdoodle.kfx.bindings.and
import de.flapdoodle.kfx.controls.charts.Serie
import de.flapdoodle.kfx.extensions.Colors
import de.flapdoodle.kfx.css.cssClassName
import de.flapdoodle.kfx.types.ranges.Range
import javafx.beans.property.SimpleObjectProperty
import javafx.css.Styleable
import javafx.geometry.Bounds
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.shape.LineTo
import javafx.scene.shape.MoveTo
import javafx.scene.shape.Path
import javafx.scene.shape.PathElement

class ChartLine<X : Any, Y : Any>(
  val serie: Serie<X, Y>,
  val xrange: Range<X>,
  val yrange: Range<Y>,
//  val spacing: DoublePropertyBase
) : Pane(), Styleable {

//    internal val spacing: SimpleStyleableDoubleProperty = object : SimpleStyleableDoubleProperty(CHART_SPACING, this, null, 2.0) {
//      override fun invalidated() {
//        requestLayout()
//      }
//    }

  private val path = Path().apply {
    stroke = serie.color
//            strokeWidth = 2.0
    cssClassName("small-chart-line")
//    minWidthProperty().bind(spacing.multiply(4))
//    minHeightProperty().bind(spacing.multiply(4))
  }

  private val coords = serie.points.map {
    SimpleObjectProperty(it).and(layoutBoundsProperty())/*.and(spacing.mapNullable { v -> v?.toDouble() ?: 0.0 })*/.map { pair, _ ->
      val usableWidht = width - insets.left - insets.right //- spacing * 2.0
      val usableHeight = height - insets.top - insets.bottom //- spacing * 2.0
      val x = xrange.offset(pair.x, usableWidht) + insets.left //+ spacing
      val y = usableHeight - yrange.offset(pair.y, usableHeight) + insets.top //+ spacing
      x to y
    }
  }

  private val lineSegments = serie.lines.flatMap {
    val linePoints = it.points.map { point ->
      SimpleObjectProperty(point).and(layoutBoundsProperty())/*.and(spacing.mapNullable { v -> v?.toDouble() ?: 0.0 })*/.map { pair, _ ->
        val usableWidht = width - insets.left - insets.right //- spacing * 2.0
        val usableHeight = height - insets.top - insets.bottom //- spacing * 2.0
        val x = xrange.offset(pair.x, usableWidht) + insets.left //+ spacing
        val y = usableHeight - yrange.offset(pair.y, usableHeight) + insets.top //+ spacing
        x to y
      }
    }

    linePoints.mapIndexed { index, coords ->
      if (index == 0) {
        MoveTo().apply {
          xProperty().bind(coords.map { it.first })
          yProperty().bind(coords.map { it.second })
        }
      } else {
        LineTo().apply {
          xProperty().bind(coords.map { it.first })
          yProperty().bind(coords.map { it.second })
        }
      }
    }
  }

  private val lineSegmentsX = coords.mapIndexed { index, coords ->
    if (index == 0) {
      MoveTo().apply {
        xProperty().bind(coords.map { it.first })
        yProperty().bind(coords.map { it.second })
      }
    } else {
      LineTo().apply {
        xProperty().bind(coords.map { it.first })
        yProperty().bind(coords.map { it.second })
      }
    }
  }

  private val points = coords.map { coords ->
    StackPane().apply {
      cssClassName("small-chart-line-symbol")
      style = "-fx-border-color: ${Colors.asCss(serie.color)}"
//                border = Border(BorderStroke(serie.color, null, null, null))
      layoutXProperty().bind(coords.and(insetsProperty()).map { c, inset -> c.first - inset.left })
      layoutYProperty().bind(coords.and(insetsProperty()).map { c, inset -> c.second - inset.top })
    }
  }

  init {
    cssClassName("small-chart-line-box")
    
    path.elements.addAll(lineSegments)
    children.add(path)
    children.addAll(points)
  }
}