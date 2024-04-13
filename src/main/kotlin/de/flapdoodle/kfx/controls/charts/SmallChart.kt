package de.flapdoodle.kfx.controls.charts

import de.flapdoodle.kfx.bindings.syncWith
import de.flapdoodle.kfx.layout.StackLikeRegion
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import javafx.beans.value.ObservableValue
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.geometry.VPos
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.shape.LineTo
import javafx.scene.shape.MoveTo
import javafx.scene.shape.Path

class SmallChart<X: Any, Y: Any>(
    series: ObservableValue<List<Serie<X, Y>>>,
    val xRangeFactory: (List<X>) -> Range<X>,
    val yRangeFactory: (List<Y>) -> Range<Y>
) : StackLikeRegion() {

    private val main = WeightGridPane().apply {
        setRowWeight(0, 1.0)
        setRowWeight(1, 0.0)
    }

    private val charts = Region().apply {
        WeightGridPane.setPosition(this, 0, 0, HPos.CENTER, VPos.CENTER)
    }
    private val labels = HBox().apply {
        alignment = Pos.CENTER
        WeightGridPane.setPosition(this, 0, 1, HPos.CENTER, VPos.CENTER)
    }

    init {
        main.children.addAll(charts, labels)
        children.add(main)

        labels.children.syncWith(series) { Label(it.label)}

        val lines: ObservableValue<List<ChartLine<X, Y>>> = series.map { list ->
            val allXY = list.flatMap { it.values }
            val allX = allXY.map { it.first }
            val allY = allXY.map { it.second }
            val xrange = xRangeFactory(allX)
            val yrange = yRangeFactory(allY)
            list.map { ChartLine(it, xrange, yrange) }
        }





//        series.addListener { _, _, list ->
//            renderChart(list)
//        }
//        renderChart(series.value)
    }

    private fun renderChart(list: List<Serie<X, Y>>) {
        // not sorted
        val allXY = list.flatMap { it.values }
        val allX = allXY.map { it.first }
        val allY = allXY.map { it.second }
        val xrange = xRangeFactory(allX)
        val yrange = yRangeFactory(allY)

        list.forEach { serie ->
            serie.values.forEach {

            }
        }
    }

    class ChartLine<X: Any, Y: Any>(
        val serie: Serie<X, Y>,
        val xrange: Range<X>,
        val yrange: Range<Y>
    ) : Path() {
        init {
            elements.addAll(
                LineTo().apply {
                   xProperty().value = 10.0
                },
                MoveTo()
            )
        }
    }
}