package de.flapdoodle.kfx.controls.charts

import de.flapdoodle.kfx.bindings.and
import de.flapdoodle.kfx.bindings.syncWith
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.extensions.mapNullable
import de.flapdoodle.kfx.layout.StackLikeRegion
import de.flapdoodle.kfx.layout.grid.WeightGridControlStyle
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.css.*
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.LineTo
import javafx.scene.shape.MoveTo
import javafx.scene.shape.Path
import javafx.scene.shape.Rectangle
import java.util.*

class SmallChart<X : Any, Y : Any>(
    series: ObservableValue<List<Serie<X, Y>>>,
    val xRangeFactory: RangeFactory<X>,
    val yRangeFactory: RangeFactory<Y>
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
            val xrange = xRangeFactory.rangeOf(allX)
            val yrange = yRangeFactory.rangeOf(allY)
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
    ) : Pane(), Styleable {

        internal val spacing = object : SimpleStyleableDoubleProperty(CHART_SPACING, this, null, 2.0) {
            override fun invalidated() {
                requestLayout()
            }
        }

        private val path = Path().apply {
            stroke = serie.color
//            strokeWidth = 2.0
            cssClassName("small-chart-line")
        }

        private val coords = serie.values.map {
            SimpleObjectProperty(it).and(layoutBoundsProperty()).and(spacing.mapNullable { v -> v?.toDouble() ?: 0.0 }).map { pair, _, spacing ->
                val x = xrange.offset(pair.first, width - insets.left - insets.right - spacing * 2.0) + insets.left + spacing
                val y = yrange.offset(pair.second, height - insets.top - insets.bottom - spacing * 2.0) + insets.top + spacing
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

        override fun getCssMetaData(): List<CssMetaData<out Styleable, *>> {
            return STYLEABLES
        }
    }


    companion object {
//        private val FACTORY = StyleablePropertyFactory<WeightGridControl>(StyleableProperties.STYLEABLES)

//        internal val CSS_HSPACE: CssMetaData<WeightGridControl, Number> = WeightGridControlStyle.FACTORY.createSizeCssMetaData(
//            WeightGridControlStyle.CSS_HSPACE_NAME,
//            { it.horizontalSpace },
//            2.0)

        private fun asCss(color: Color): String {
            return String.format(
                "#%02X%02X%02X",
                (color.red * 255).toInt(),
                (color.green * 255).toInt(),
                (color.blue * 255).toInt()
            )
        }

        val CHART_SPACING: CssMetaData<ChartLine<out Any, out Any>, Number> = object : CssMetaData<ChartLine<out Any, out Any>, Number>("-chart-spacing",StyleConverter.getSizeConverter()) {
            override fun isSettable(styleable: ChartLine<*, *>): Boolean {
                return !styleable.spacing.isBound
            }

            override fun getStyleableProperty(styleable: ChartLine<*, *>): StyleableProperty<Number> {
                return styleable.spacing
            }
        }

        val STYLEABLES = emptyList<CssMetaData<out Styleable, *>>() + Pane.getClassCssMetaData() + CHART_SPACING

//        init {
//            final List < CssMetaData <? extends Styleable, ?>> styleables = new ArrayList<>(Node.getClassCssMetaData());
//            styleables.add(GRID_COLOR);
//            STYLEABLES = Collections.unmodifiableList(styleables);
//        }
    }
}