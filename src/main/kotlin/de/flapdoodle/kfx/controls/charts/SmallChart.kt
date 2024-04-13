package de.flapdoodle.kfx.controls.charts

import de.flapdoodle.kfx.bindings.ObjectBindings
import de.flapdoodle.kfx.bindings.and
import de.flapdoodle.kfx.bindings.syncWith
import de.flapdoodle.kfx.extensions.asDouble
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.extensions.layoutPosition
import de.flapdoodle.kfx.layout.StackLikeRegion
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.geometry.VPos
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.LineTo
import javafx.scene.shape.MoveTo
import javafx.scene.shape.Path
import javafx.scene.shape.PathElement
import javafx.scene.shape.Rectangle

class SmallChart<X : Any, Y : Any>(
    series: ObservableValue<List<Serie<X, Y>>>,
    val xRangeFactory: (List<X>) -> Range<X>,
    val yRangeFactory: (List<Y>) -> Range<Y>
) : StackLikeRegion() {

    private val main = WeightGridPane().apply {
        setRowWeight(0, 1.0)
        setRowWeight(1, 0.0)
    }

    private val charts = PaneLike().apply {
        WeightGridPane.setPosition(this, 0, 0, HPos.CENTER, VPos.CENTER)
        clipProperty().bind(layoutBoundsProperty().map { Rectangle(it.minX, it.minY, it.width, it.height) })
    }
    private val labels = HBox().apply {
        cssClassName("small-chart-legends")
        alignment = Pos.CENTER
        WeightGridPane.setPosition(this, 0, 1, HPos.CENTER, VPos.CENTER)
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
            val xrange = xRangeFactory(allX)
            val yrange = yRangeFactory(allY)
            list.map { ChartLine(it, xrange, yrange) }
        }

        charts.children.syncWith(lines) { it }
    }

    class Legend(name: String, color: Color): HBox() {
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

    class ChartLine<X : Any, Y : Any>(
        val serie: Serie<X, Y>,
        val xrange: Range<X>,
        val yrange: Range<Y>
    ) : Pane() {

        private val path = Path().apply {
            stroke = serie.color
//            strokeWidth = 2.0
            cssClassName("small-chart-line")
        }

        private val coords = serie.values.map {
            SimpleObjectProperty(it).and(layoutBoundsProperty()).and(insetsProperty()).map { pair, layout, insets ->
                val x = xrange.offset(pair.first, layout.width - insets.left - insets.right) + insets.left
                val y = yrange.offset(pair.second, layout.height - insets.top - insets.bottom) + insets.top
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


    companion object {
        private fun asCss(color: Color): String {
            return String.format(
                "#%02X%02X%02X",
                (color.red * 255).toInt(),
                (color.green * 255).toInt(),
                (color.blue * 255).toInt()
            )
        }
    }
}