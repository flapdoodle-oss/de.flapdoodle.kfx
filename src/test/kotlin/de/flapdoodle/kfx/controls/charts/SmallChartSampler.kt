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
import de.flapdoodle.kfx.tasks.PlatformTasks
import de.flapdoodle.kfx.tasks.TaskFactory
import de.flapdoodle.kfx.tasks.Tasks
import de.flapdoodle.kfx.tasks.Timelines
import de.flapdoodle.kfx.types.ranges.RangeFactories
import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.application.Application
import javafx.beans.property.SimpleObjectProperty
import javafx.concurrent.Task
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.util.Duration
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
                    children.add(Button("clear").apply {
                        onAction = EventHandler {
                            series.value = emptyList()
                        }
                    })
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
                                ), listOf(
                                    Serie.Line(
                                        pointsOf(
                                            now to 100.0,
                                            now.plusDays(1) to 110.0,
                                            now.plusDays(32) to 70.0,
                                            now.plusDays(35) to 200.0,
                                            now.plusDays(75) to 600.0,
                                            now.plusDays(235) to -10.0,
                                            now.plusDays(310) to 20.0,
                                        )
                                    )
                                )
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
                                    Serie.Line(
                                        pointsOf(
                                            now.minusDays(5) to 80.0,
                                            now.plusDays(5) to 80.0,
                                        )
                                    ),
                                    Serie.Line(
                                        pointsOf(
                                            now.plusDays(5) to 60.0,
                                            now.plusDays(20) to 60.0,
                                        )
                                    ),
                                    Serie.Line(
                                        pointsOf(
                                            now.plusDays(20) to 70.0,
                                            now.plusDays(22) to 70.0,
                                        )
                                    ),
                                )
                            )

                        }
                    })
                }
            }

            val addA = Runnable {
                series.value = series.value + Serie(
                    "a", Color.RED, pointsOf(
                        now to 100.0,
                        now.plusDays(1) to 110.0,
                        now.plusDays(32) to 70.0,
                        now.plusDays(35) to 200.0,
                        now.plusDays(75) to 600.0,
                        now.plusDays(235) to -10.0,
                        now.plusDays(310) to 20.0,
                    ), listOf(
                        Serie.Line(
                            pointsOf(
                                now to 100.0,
                                now.plusDays(1) to 110.0,
                                now.plusDays(32) to 70.0,
                                now.plusDays(35) to 200.0,
                                now.plusDays(75) to 600.0,
                                now.plusDays(235) to -10.0,
                                now.plusDays(310) to 20.0,
                            )
                        )
                    )
                )
            }

            val addB = Runnable {
                series.value = series.value + Serie(
                    "b", Color.BLUE, pointsOf(
                        now.minusDays(5) to 80.0,
                        now.plusDays(5) to 60.0,
                        now.plusDays(20) to 70.0,
                        now.plusDays(22) to 80.0,
                    ), listOf(
                        Serie.Line(
                            pointsOf(
                                now.minusDays(5) to 80.0,
                                now.plusDays(5) to 80.0,
                            )
                        ),
                        Serie.Line(
                            pointsOf(
                                now.plusDays(5) to 60.0,
                                now.plusDays(20) to 60.0,
                            )
                        ),
                        Serie.Line(
                            pointsOf(
                                now.plusDays(20) to 70.0,
                                now.plusDays(22) to 70.0,
                            )
                        ),
                    )
                )
            }

            val clearAll = Runnable{
                series.value = emptyList()
            }

            val addAFactory = TaskFactory<Void> { PlatformTasks.runLater(addA) }
            val addBFactory = TaskFactory<Void> { PlatformTasks.runLater(addB) }
            val clearAllFactory = TaskFactory<Void> { PlatformTasks.runLater(clearAll) }

            val service = Tasks.executeAll(addAFactory, addBFactory, clearAllFactory)
                .waitFor(Duration.millis(1500.0))
                .nextAfter(Duration.millis(500.0))
                .repeating()
                .asScheduledService()

            val timeline = Timelines.executeAll(addA.named("addA"), addB.named("addB"), clearAll.named("clearAll"))
                .waitFor(Duration.millis(1500.0))
                .nextAfter(Duration.millis(5.0))
                .repeating()
                .asTimeline()

//            val timeline = Timeline(
//                KeyFrame(Duration.millis(100.0), { event ->
//                    println("event: $event")
//                }))
//            timeline.cycleCount = Animation.INDEFINITE


            stage.scene = Scene(all, 800.0, 600.0)

            if (true) {
                println("--- looper ---")
//                service.delay = Duration.millis(1500.0)
//                service.period = Duration.millis(500.0)
//                service.start()
                timeline.play()
                println("--- looper done ---")
            }

            stage.show()

//            Thread.sleep(10000)

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

private fun Runnable.named(name: String): Runnable {
    val delegate = this
    return object : Runnable {
        override fun run() {
            delegate.run()
        }

        override fun toString(): String {
            return name
        }
    }
}
