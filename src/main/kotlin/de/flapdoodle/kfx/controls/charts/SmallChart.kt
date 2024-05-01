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

import de.flapdoodle.kfx.bindings.*
import de.flapdoodle.kfx.bindings.css.NumberCssMetaData
import de.flapdoodle.kfx.extensions.Colors
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.layout.StackLikeRegion
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.kfx.types.Direction
import javafx.beans.value.ObservableValue
import javafx.css.*
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.geometry.VPos
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class SmallChart<X : Any, Y : Any>(
  series: ObservableValue<List<Serie<X, Y>>>,
  val xRangeFactory: RangeFactory<X>,
  val yRangeFactory: RangeFactory<Y>
) : StackLikeRegion() {

  internal val chartSpacing = CHART_SPACING.asProperty(5.0) {
    requestLayout()
  }

  private val lines = series.map { list ->
    val allXY = list.flatMap { it.values }
    val allX = allXY.map { it.first }
    val allY = allXY.map { it.second }
    val xrange = xRangeFactory.rangeOf(allX)
    val yrange = yRangeFactory.rangeOf(allY)
    list.map { ChartLine(it, xrange, yrange).apply {
//      layoutX = 0.0
//      layoutY = 0.0
    } }
  }

  private val xRange = series.map { list ->
    val allXY = list.flatMap { it.values }
    val allX = allXY.map { it.first }
    xRangeFactory.rangeOf(allX)
  }

  private val yRange = series.map { list ->
    val allXY = list.flatMap { it.values }
    val allY = allXY.map { it.second }
    yRangeFactory.rangeOf(allY)
  }

  private val main = WeightGridPane().apply {
    setRowWeight(0, 0.0)
    setRowWeight(1, 1.0)
    setRowWeight(2, 0.0)
    setRowWeight(3, 0.0)

    setColumnWeight(0, 0.0)
    setColumnWeight(1, 1.0)
    setColumnWeight(2, 0.0)
  }

  private val charts = PaneLike().apply {
    WeightGridPane.setPosition(this, 1, 1, HPos.CENTER, VPos.CENTER)
    clipProperty().bind(layoutBoundsProperty().map { Rectangle(it.minX, it.minY, it.width, it.height) })
  }

  private val topScale = Scale(xRange, Direction.TOP).apply {
    WeightGridPane.setPosition(this, 1, 0, HPos.CENTER, VPos.CENTER)
  }

  private val bottomScale = Scale(xRange, Direction.BOTTOM).apply {
    WeightGridPane.setPosition(this, 1, 2, HPos.CENTER, VPos.CENTER)
  }

  private val leftScale = Scale(yRange, Direction.LEFT).apply {
    WeightGridPane.setPosition(this, 0, 1, HPos.CENTER, VPos.CENTER)
  }

  private val rightScale = Scale(yRange, Direction.RIGHT).apply {
    WeightGridPane.setPosition(this, 2, 1, HPos.CENTER, VPos.CENTER)
  }

  private val labels = HBox().apply {
    cssClassName("small-chart-legends")
    alignment = Pos.CENTER
    WeightGridPane.setPosition(this, 1, 3, HPos.CENTER, VPos.CENTER)
  }

  init {
    bindCss("small-chart")

    main.children.addAll(charts, topScale, bottomScale, leftScale, rightScale, labels)
    children.add(main)

    labels.children.syncWith(series) { Legend(it.label, it.color) }
    charts.children.syncWith(lines) { it }
  }

  class Legend(name: String, color: Color) : HBox() {
    init {
      cssClassName("small-chart-legend")
      isFillHeight = false
      alignment = Pos.CENTER
      children.addAll(
        Label(name),
        StackPane().apply {
          cssClassName("small-chart-line-symbol")
          style = "-fx-border-color: ${Colors.asCss(color)}"

        })
    }
  }

  override fun getCssMetaData(): List<CssMetaData<out Styleable, *>> {
    return STYLEABLES
  }

  companion object {
    val CHART_SPACING: NumberCssMetaData<SmallChart<out Any, out Any>> = NumberCssMetaData("-chart-spacing", SmallChart<out Any, out Any>::chartSpacing)

    val STYLEABLES = emptyList<CssMetaData<out Styleable, *>>() + Pane.getClassCssMetaData() + CHART_SPACING
  }
}

