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

import de.flapdoodle.kfx.converters.DefaultValidatingConverterFactory
import de.flapdoodle.kfx.types.ranges.RangeFactories
import javafx.application.Application
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Stage
import java.time.LocalDate
import java.util.*

class SmallChartSampler {
    class Sample : Application() {
        override fun start(stage: Stage) {
            val now = LocalDate.now()

            val series = SimpleObjectProperty(emptyList<Serie<LocalDate, Double>>())
            series.value = listOf(
                Serie(
                    "0", Color.ORANGE, pointsOf(
                        now.plusDays(7) to 95.0,
                    ), emptyList()
                )
            )
            val content = SmallChart(
                series,
                RangeFactories.localDate(),
                RangeFactories.number(Double::class),
                DefaultValidatingConverterFactory.converter(LocalDate::class, Locale.GERMANY),
                DefaultValidatingConverterFactory.converter(Double::class, Locale.GERMANY)
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
                                "a", Color.RED, pointsOf(
                                    now to 100.0,
                                    now.plusDays(1) to 110.0,
                                    now.plusDays(32) to 70.0,
                                    now.plusDays(35) to 200.0,
                                    now.plusDays(75) to 600.0,
                                    now.plusDays(235) to -10.0,
                                    now.plusDays(310) to 20.0,
                                ), listOf(Serie.Line(pointsOf(
                                    now to 100.0,
                                    now.plusDays(1) to 110.0,
                                    now.plusDays(32) to 70.0,
                                    now.plusDays(35) to 200.0,
                                    now.plusDays(75) to 600.0,
                                    now.plusDays(235) to -10.0,
                                    now.plusDays(310) to 20.0,
                                )))
                            )

                        }
                    })
                    children.add(Button("+b").apply {
                        onAction = EventHandler {
                            series.value = series.value + Serie(
                                "b", Color.BLUE, pointsOf(
                                    now.minusDays(5) to 80.0,
                                    now.plusDays(5) to 60.0,
                                    now.plusDays(20) to 70.0,
                                    now.plusDays(22) to 80.0,
                                ), listOf(
                                    Serie.Line(pointsOf(
                                        now.minusDays(5) to 80.0,
                                        now.plusDays(5) to 80.0,
                                    )),
                                    Serie.Line(pointsOf(
                                        now.plusDays(5) to 60.0,
                                        now.plusDays(20) to 60.0,
                                    )),
                                    Serie.Line(pointsOf(
                                        now.plusDays(20) to 70.0,
                                        now.plusDays(22) to 70.0,
                                    )),
                                )
                            )

                        }
                    })
                }
            }

            stage.scene = Scene(all, 800.0, 600.0)
            stage.show()
        }

    }


    companion object {
        private fun <X, Y> pointsOf(vararg list: Pair<X, Y>): List<Serie.Point<X, Y>> {
            return list.map { Serie.Point(it.first, it.second) }
        }

        @JvmStatic
        fun main(args: Array<String>) {
            Application.launch(Sample::class.java, *args)
        }
    }
}