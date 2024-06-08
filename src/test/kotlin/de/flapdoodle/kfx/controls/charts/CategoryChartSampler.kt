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

import de.flapdoodle.kfx.converters.Converters
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

class CategoryChartSampler {
    class Sample : Application() {
        override fun start(stage: Stage) {
            val now = LocalDate.now()

            val series = SimpleObjectProperty(emptyList<Serie<String, Double>>())
            series.value = listOf(
                Serie(
                    "0", Color.ORANGE, listOf(
                        "007" to 95.0,
                    )
                )
            )
            val content = SmallChart(
                series,
                RangeFactories.category(),
                RangeFactories.number(Double::class),
                Converters.validatingFor(String::class, Locale.GERMANY),
                Converters.validatingFor(Double::class, Locale.GERMANY)
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
                                    "000" to 100.0,
                                    "001" to 110.0,
                                    "032" to 70.0,
                                    "035" to 200.0,
                                    "075" to 600.0,
                                    "235" to -10.0,
                                    "310" to 20.0,
                                )
                            )

                        }
                    })
                    children.add(Button("+b").apply {
                        onAction = EventHandler {
                            series.value = series.value + Serie(
                                "b", Color.BLUE, listOf(
                                    "-05" to 80.0,
                                    "005" to 60.0,
                                    "020" to 70.0,
                                    "022" to 80.0,
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
        @JvmStatic
        fun main(args: Array<String>) {
            Application.launch(Sample::class.java, *args)
        }
    }
}