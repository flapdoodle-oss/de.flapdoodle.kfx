package de.flapdoodle.kfx.controls.charts.parts

import de.flapdoodle.kfx.bindings.and
import de.flapdoodle.kfx.controls.charts.ranges.Range
import de.flapdoodle.kfx.controls.charts.Serie
import de.flapdoodle.kfx.extensions.Colors
import de.flapdoodle.kfx.extensions.cssClassName
import javafx.beans.property.SimpleObjectProperty
import javafx.css.Styleable
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.shape.LineTo
import javafx.scene.shape.MoveTo
import javafx.scene.shape.Path

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

  private val coords = serie.values.map {
    SimpleObjectProperty(it).and(layoutBoundsProperty())/*.and(spacing.mapNullable { v -> v?.toDouble() ?: 0.0 })*/.map { pair, _ ->
      val usableWidht = width - insets.left - insets.right //- spacing * 2.0
      val usableHeight = height - insets.top - insets.bottom //- spacing * 2.0
      val x = xrange.offset(pair.first, usableWidht) + insets.left //+ spacing
      val y = usableHeight - yrange.offset(pair.second, usableHeight) + insets.top //+ spacing
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