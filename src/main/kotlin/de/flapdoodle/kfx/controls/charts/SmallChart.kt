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
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.extensions.mapNullable
import de.flapdoodle.kfx.layout.StackLikeRegion
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import javafx.beans.property.DoublePropertyBase
import javafx.beans.property.SimpleObjectProperty
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
import javafx.scene.shape.LineTo
import javafx.scene.shape.MoveTo
import javafx.scene.shape.Path
import javafx.scene.shape.Rectangle

class SmallChart<X : Any, Y : Any>(
  series: ObservableValue<List<Serie<X, Y>>>,
  val xRangeFactory: RangeFactory<X>,
  val yRangeFactory: RangeFactory<Y>
) : StackLikeRegion() {

  internal val chartSpacing = object : SimpleStyleableDoubleProperty(CHART_SPACING, this, null, 2.0) {
    override fun invalidated() {
      requestLayout()
    }
  }

  internal val scaleSpacing = object : SimpleStyleableDoubleProperty(SCALE_SPACING, this, null, 2.0) {
    override fun invalidated() {
      requestLayout()
    }
  }

  internal val scaleLength = object : SimpleStyleableDoubleProperty(SCALE_LENGTH, this, null, 2.0) {
    override fun invalidated() {
      requestLayout()
    }
  }

  private val main = WeightGridPane().apply {
    setRowWeight(0, 1.0)
    setRowWeight(1, 0.0)
    setRowWeight(2, 0.0)
  }

  private val charts = PaneLike().apply {
    WeightGridPane.setPosition(this, 0, 0, HPos.CENTER, VPos.CENTER)
    clipProperty().bind(layoutBoundsProperty().map { Rectangle(it.minX, it.minY, it.width, it.height) })
  }

  private val labels = HBox().apply {
    cssClassName("small-chart-legends")
    alignment = Pos.CENTER
    WeightGridPane.setPosition(this, 0, 2, HPos.CENTER, VPos.CENTER)
  }

  init {
    bindCss("small-chart")

    main.children.addAll(charts, labels)
    children.add(main)

    labels.children.syncWith(series) { Legend(it.label, it.color) }

    val lines = series.map { list ->
      val allXY = list.flatMap { it.values }
      val allX = allXY.map { it.first }
      val allY = allXY.map { it.second }
      val xrange = xRangeFactory.rangeOf(allX)
      val yrange = yRangeFactory.rangeOf(allY)
      list.map { ChartLine(it, xrange, yrange, chartSpacing) }
    }

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
          style = "-fx-border-color: ${asCss(color)}"

        })
    }
  }

  class Scale<T>(
    private val range: Range<T>,
    private val spacing: DoublePropertyBase
  ): Pane(), Styleable {
    private val spacingNonNull: ObservableValue<out Number> = spacing.mapToDouble { v -> v?.toDouble() ?: 10.0 }

    private val path = Path().apply {
      stroke = Color.BLACK
//            strokeWidth = 2.0
      cssClassName("small-chart-scale")
      minWidthProperty().bind(spacing.multiply(4))
      minHeightProperty().bind(spacing.multiply(4))
    }

    internal val length = object : SimpleStyleableDoubleProperty(SCALE_LENGTH, this, null, 10.0) {
      override fun invalidated() {
        requestLayout()
      }
    }

    private val ticks = DoubleBindings.merge(spacingNonNull, widthProperty()) { space, width ->
      val maxTicks = (width / space).toInt()
      val list = range.ticks(maxTicks).firstOrNull()?.list ?: emptyList()

      list
    }

//    private val x = ObjectBindings.merge(ticks, layoutBoundsProperty()) { t, l ->
//        val usableWidht = width - insets.left - insets.right - spacing * 2.0
//        val usableHeight = height - insets.top - insets.bottom - spacing * 2.0
//        val x = xrange.offset(pair.first, usableWidht) + insets.left + spacing
//        val y = usableHeight - yrange.offset(pair.second, usableHeight) + insets.top + spacing
//        x to y
//    }


    init {
//      path.elements.syncWith(elements)
    }
//
//    private val coords = ticks.map { it: List<T> ->
////      SimpleObjectProperty(it).and(layoutBoundsProperty()).and(spacing.mapNullable { v -> v?.toDouble() ?: 0.0 }).map { pair, _, spacing ->
////        val usableWidht = width - insets.left - insets.right - spacing * 2.0
////        val usableHeight = height - insets.top - insets.bottom - spacing * 2.0
////        val x = xrange.offset(pair.first, usableWidht) + insets.left + spacing
////        val y = usableHeight - yrange.offset(pair.second, usableHeight) + insets.top + spacing
////        x to y
////      }
//    }




  }

  class ChartLine<X : Any, Y : Any>(
    val serie: Serie<X, Y>,
    val xrange: Range<X>,
    val yrange: Range<Y>,
    val spacing: DoublePropertyBase
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
      minWidthProperty().bind(spacing.multiply(4))
      minHeightProperty().bind(spacing.multiply(4))
    }

    private val coords = serie.values.map {
      SimpleObjectProperty(it).and(layoutBoundsProperty()).and(spacing.mapNullable { v -> v?.toDouble() ?: 0.0 }).map { pair, _, spacing ->
        val usableWidht = width - insets.left - insets.right - spacing * 2.0
        val usableHeight = height - insets.top - insets.bottom - spacing * 2.0
        val x = xrange.offset(pair.first, usableWidht) + insets.left + spacing
        val y = usableHeight - yrange.offset(pair.second, usableHeight) + insets.top + spacing
        x to y
      }
    }

    private val lineSegments = coords.mapIndexed { index, coords ->
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
        style = "-fx-border-color: ${asCss(serie.color)}"
//                border = Border(BorderStroke(serie.color, null, null, null))
        layoutXProperty().bind(coords.and(insetsProperty()).map { c, inset -> c.first - inset.left })
        layoutYProperty().bind(coords.and(insetsProperty()).map { c, inset -> c.second - inset.right })
      }
    }

    init {
      cssClassName("small-chart-line-box")

      path.elements.addAll(lineSegments)
      children.add(path)
      children.addAll(points)
    }
  }

  override fun getCssMetaData(): List<CssMetaData<out Styleable, *>> {
    return STYLEABLES
  }

  companion object {
    private fun asCss(color: Color): String {
      return String.format(
        "#%02X%02X%02X",
        (color.red * 255).toInt(),
        (color.green * 255).toInt(),
        (color.blue * 255).toInt()
      )
    }

    val CHART_SPACING: CssMetaData<SmallChart<out Any, out Any>, Number> =
      object : CssMetaData<SmallChart<out Any, out Any>, Number>("-chart-spacing", StyleConverter.getSizeConverter()) {
        override fun isSettable(styleable: SmallChart<*, *>): Boolean {
          return !styleable.chartSpacing.isBound
        }

        override fun getStyleableProperty(styleable: SmallChart<*, *>): StyleableProperty<Number> {
          return styleable.chartSpacing
        }
      }

    val SCALE_SPACING: CssMetaData<SmallChart<*, *>, Number> =
      object : CssMetaData<SmallChart<*, *>, Number>("-scale-spacing", StyleConverter.getSizeConverter()) {
        override fun isSettable(styleable: SmallChart<*, *>): Boolean {
          return !styleable.scaleSpacing.isBound
        }

        override fun getStyleableProperty(styleable: SmallChart<*, *>): StyleableProperty<Number> {
          return styleable.scaleSpacing
        }
      }

    val SCALE_LENGTH: CssMetaData<SmallChart<*, *>, Number> =
      object : CssMetaData<SmallChart<*, *>, Number>("-scale-length", StyleConverter.getSizeConverter()) {
        override fun isSettable(styleable: SmallChart<*, *>): Boolean {
          return !styleable.scaleLength.isBound
        }

        override fun getStyleableProperty(styleable: SmallChart<*, *>): StyleableProperty<Number> {
          return styleable.scaleLength
        }
      }

    // ChartLine extends Pane
    val STYLEABLES = emptyList<CssMetaData<out Styleable, *>>() + Pane.getClassCssMetaData() + CHART_SPACING + SCALE_SPACING + SCALE_LENGTH
  }
}