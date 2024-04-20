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
package de.flapdoodle.kfx.controls.virtual

import de.flapdoodle.kfx.controls.charts.Range
import de.flapdoodle.kfx.controls.charts.Serie
import de.flapdoodle.kfx.controls.charts.SmallChart
import de.flapdoodle.kfx.extensions.BoundingBoxes
import de.flapdoodle.kfx.extensions.mapNullable
import de.flapdoodle.kfx.extensions.withAnchors
import de.flapdoodle.kfx.layout.StackLikeRegion
import de.flapdoodle.kfx.sampler.AbsolutePane
import javafx.application.Application
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.geometry.Bounds
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.LineTo
import javafx.scene.shape.MoveTo
import javafx.scene.shape.Path
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class VirtualViewSampler {
    class Sample : Application() {
        override fun start(stage: Stage) {
            val viewBound = Rectangle().apply {
                width = 100.0
                height = 100.0
                fill = Color.rgb(0,0,0, 0.3)
            }
//            val underlay = Pane().apply {
//                children.add(viewBound)
//                withAnchors(all = 0.0)
//            }

            val content = VirtualView(
                content = Pane().apply {
                    children.add(Rectangle(20.0, 30.0).apply {
                        fill = Color.RED
                        x = 200.0
                        y = 50.0
                    })
                    children.add(Path().apply {
                        stroke = Color.GREEN
                        elements.addAll(
                            MoveTo(-30.0, -10.0),
                            LineTo(20.0, 20.0),
                            LineTo(40.0, -10.0)
                        )
                    })
                    children.add(viewBound.apply {
                        isManaged = false
                    })
                },
                boundsInParentProperty = { node -> node.parentProperty().mapNullable {
                    BoundingBoxes.reduceBounds(node.children) { c -> if (c.isManaged) c.boundsInParent else null }
                } },
                subscribeToViewBounds = {
                    it.addListener { observable, oldValue, newValue ->
                        viewBound.x = newValue.minX + 5.0
                        viewBound.y = newValue.minY + 5.0
                        viewBound.width = newValue.width - 10.0
                        viewBound.height = newValue.height - 10.0
                    }
                }
            ).withAnchors(all = 0.0)

            val layers = AnchorPane().apply {
                children.addAll(content)
            }

            val all = BorderPane().apply {
                center = layers
                bottom = VBox().apply {
                    children.add(Button("+").apply {
                        onAction = EventHandler {

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