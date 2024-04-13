package de.flapdoodle.kfx.controls.charts

import de.flapdoodle.kfx.bindings.toObservable
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.extensions.cssClassName
import javafx.application.Application
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Stage
import java.time.Duration
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class SmallChartSampler {
    class Sample : Application() {
        override fun start(stage: Stage) {
            val now = LocalDate.now()

            val series = SimpleObjectProperty(emptyList<Serie<LocalDate, Number>>())
            series.value = listOf(
                Serie(
                    "0", Color.ORANGE, listOf(
                        now.plusDays(7) to 95.0,
                    )
                )
            )
            val content = SmallChart(
                series,
                RangeFactories.localDate(),
                RangeFactories.number()
            )
//      val content = ColorableLineChart<Number, Number>(x, y, series) {
//        it -> colorMap[it.name] ?: Color.BLACK
//      }

            val all = BorderPane().apply {
                center = content
                bottom = VBox().apply {
                    children.add(Button("+a").apply {
                        onAction = EventHandler {
                            series.value = series.value + Serie(
                                "a", Color.RED, listOf(
                                    now to 100.0,
                                    now.plusDays(1) to 110.0,
                                    now.plusDays(32) to 70.0,
                                    now.plusDays(35) to 200.0,
                                )
                            )

                        }
                    })
                    children.add(Button("+b").apply {
                        onAction = EventHandler {
                            series.value = series.value + Serie(
                                "b", Color.BLUE, listOf(
                                    now.minusDays(5) to 80.0,
                                    now.plusDays(5) to 60.0,
                                    now.plusDays(20) to 70.0,
                                    now.plusDays(22) to 80.0,
                                )
                            )

                        }
                    })
                }
            }

            stage.scene = Scene(all, 800.0, 600.0)
            stage.show()
        }

        private fun numberRange(): (List<Number>) -> Range<Number> {
            return { list ->
                // liste kann leer sein oder ein element beinhalten
                val asDouble = list.map { it.toDouble() }
                val min = asDouble.min()
                val max = asDouble.max()
                val dist = max - min

                Range { value, scale ->
                    val valueDist = value.toDouble() - min
                    scale * valueDist/dist
                }
            }
        }

        private fun localDateRange(): (List<LocalDate>) -> Range<LocalDate> {
            return { list ->
                // liste kann leer sein oder ein element beinhalten
                val min = list.min()
                val max = list.max()

                val dist = ChronoUnit.DAYS.between(min, max)
//                val distance = max.
                Range { value, scale ->
                    val valueDist = ChronoUnit.DAYS.between(min, value)
                    scale * valueDist/dist
                }
            }
        }
    }


    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Application.launch(Sample::class.java, *args)
        }
    }
}