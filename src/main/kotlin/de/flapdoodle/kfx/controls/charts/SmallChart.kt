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
package de.flapdoodle.kfx.controls.charts

import de.flapdoodle.kfx.bindings.ObjectBindings
import de.flapdoodle.kfx.bindings.syncWith
import de.flapdoodle.kfx.controls.charts.parts.ChartLine
import de.flapdoodle.kfx.controls.charts.parts.Scale
import de.flapdoodle.kfx.converters.ValidatingConverter
import de.flapdoodle.kfx.extensions.Colors
import de.flapdoodle.kfx.css.bindCss
import de.flapdoodle.kfx.css.cssClassName
import de.flapdoodle.kfx.layout.StackLikeRegion
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.kfx.types.Direction
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.kfx.types.ranges.RangeFactory
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.geometry.VPos
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class SmallChart<X : Any, Y : Any>(
  series: ObservableValue<List<Serie<X, Y>>>,
  val xRangeFactory: RangeFactory<X>,
  val yRangeFactory: RangeFactory<Y>,
  val xConverter: ValidatingConverter<X>,
  val yConverter: ValidatingConverter<Y>,
  val showScaleAt: Set<Direction> = setOf(Direction.LEFT, Direction.BOTTOM)
) : StackLikeRegion() {

  private val filterSet = SimpleObjectProperty<Set<Id<Serie<*,*>>>>(emptySet())
  private val filtered = ObjectBindings.merge(series, filterSet) { s, f ->
    s.filter { !f.contains(it.id) }
  }
//  private val filtered = series.and().map { it }

  private val lines = filtered.map { list ->
    val allXY = list.flatMap { it.points } + list.flatMap { it.lines.flatMap { it.points } }
    val allX = allXY.map { it.x }
    val allY = allXY.map { it.y }
    val xrange = xRangeFactory.rangeOf(allX)
    val yrange = yRangeFactory.rangeOf(allY)
    list.map { ChartLine(it, xrange, yrange).apply {
//      layoutX = 0.0
//      layoutY = 0.0
    } }
  }

  private val xRange = filtered.map { list ->
    val allXY = list.flatMap { it.points } + list.flatMap { it.lines.flatMap { it.points } }
    val allX = allXY.map { it.x }
    xRangeFactory.rangeOf(allX)
  }

  private val yRange = filtered.map { list ->
    val allXY = list.flatMap { it.points } + list.flatMap { it.lines.flatMap { it.points } }
    val allY = allXY.map { it.y }
    yRangeFactory.rangeOf(allY)
  }

  private val topScale = Scale(xConverter, xRange, Direction.TOP).apply {
    WeightGridPane.setPosition(this, 1, 0, HPos.CENTER, VPos.CENTER)
  }

  private val leftScale = Scale(yConverter, yRange, Direction.LEFT).apply {
    WeightGridPane.setPosition(this, 0, 1, HPos.CENTER, VPos.CENTER)
  }

  private val charts = PaneLike().apply {
    WeightGridPane.setPosition(this, 1, 1, HPos.CENTER, VPos.CENTER)
    clipProperty().bind(layoutBoundsProperty().map { Rectangle(it.minX, it.minY, it.width, it.height) })

    children.syncWith(lines) { it }
  }

  private val rightScale = Scale(yConverter, yRange, Direction.RIGHT).apply {
    WeightGridPane.setPosition(this, 2, 1, HPos.CENTER, VPos.CENTER)
  }

  private val bottomScale = Scale(xConverter, xRange, Direction.BOTTOM).apply {
    WeightGridPane.setPosition(this, 1, 2, HPos.CENTER, VPos.CENTER)
  }

  private val labels = HBox().apply {
    cssClassName("small-chart-legends")
    alignment = Pos.CENTER
    WeightGridPane.setPosition(this, 0, 1, HPos.CENTER, VPos.CENTER)

    children.syncWith(series) { Legend(it.label, it.color, it.id, filterSet) }
  }

  private val chartArea = WeightGridPane().apply {
    cssClassName("small-chart-charts")
    rowWeight(0, 0.0)
    rowWeight(1, 1.0)
    rowWeight(2, 0.0)

    columnWeight(0, 0.0)
    columnWeight(1, 1.0)
    columnWeight(2, 0.0)

    children.addAll(charts)
    if (showScaleAt.contains(Direction.LEFT)) children.add(leftScale)
    if (showScaleAt.contains(Direction.RIGHT)) children.add(rightScale)
    if (showScaleAt.contains(Direction.TOP)) children.add(topScale)
    if (showScaleAt.contains(Direction.BOTTOM)) children.add(bottomScale)
  }

  private val main = WeightGridPane().apply {
    rowWeight(0, 1.0)
    rowWeight(1, 0.0)

    children.addAll(chartArea, labels)
  }


  init {
    bindCss("small-chart")

    series.addListener { observable, oldValue, newValue ->
      filterSet.value = emptySet()
    }

    children.add(main)
  }

  class Legend(
    name: String,
    color: Color,
    id: Id<Serie<*, *>>,
    filterSet: SimpleObjectProperty<Set<Id<Serie<*, *>>>>
  ) : HBox() {
    init {
      cssClassName("small-chart-legend")
      isFillHeight = false
      alignment = Pos.CENTER
      children.addAll(
        StackPane().apply {
          cssClassName("small-chart-line-symbol")
          style = "-fx-border-color: ${Colors.asCss(color)}"

        },
        Label(name))

      onMouseClicked = EventHandler {
        if (filterSet.value.contains(id)) {
          filterSet.value = filterSet.value - id
        } else {
          filterSet.value = filterSet.value + id
        }
      }

      filterSet.addListener { observable, oldValue, newValue ->
        Styles.Hidden.set(this, newValue.contains(id))
      }
    }
  }
}

