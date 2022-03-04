/**
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
package de.flapdoodle.kfx.sampler

import de.flapdoodle.kfx.clone.BoxFactory
import de.flapdoodle.kfx.graph.nodes.ResizablePane
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.kfx.layout.virtual.PanZoomPanel
import de.flapdoodle.kfx.events.SharedEventLock
import de.flapdoodle.kfx.layout.layer.LayerPane
import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.stage.Stage

class PanningWindowsSampler : Application() {

    override fun start(stage: Stage) {
        val sharedEventLock = SharedEventLock()

        val testee = PanZoomPanel(sharedEventLock).apply {
            setContent(otherSampleContent(sharedEventLock))
            WeightGridPane.setPosition(this, 1, 0)
        }

        stage.scene = Scene(WeightGridPane().apply {
//            children.add(template)
            children.add(testee)

            setColumnWeight(0, 1.0)
            setColumnWeight(1, 1.0)
            setRowWeight(0, 1.0)
        }, 400.0, 400.0)
        stage.scene.stylesheets.add(getStyleResource())
        stage.show()
    }

    fun getStyleResource(): String {
        val resource = PanningWindowsSampler::class.java.getResource("panning-windows-sampler.css") ?: throw IllegalArgumentException("stylesheet not found")
        return resource.toExternalForm() ?: throw IllegalArgumentException("could not get external form for $resource")
    }


    fun otherSampleContent(sharedEventLock: SharedEventLock): LayerPane<String> {
        return LayerPane(setOf("A","B","C")).apply {
            addAll("B", ResizablePane(sharedEventLock))
            
            addAll("A", Pane().apply {
                this.layoutX = 0.0
                this.layoutY = 0.0
                this.minWidth = 420.0
                this.minHeight = 180.0
                this.border = Border(BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, CornerRadii(1.0), BorderWidths(1.0)))
            })

            addAll("A", Pane().apply {
                this.layoutX = 50.0
                this.layoutY = 150.0
                this.minWidth = 80.0
                this.minHeight = 40.0
                this.border = Border(BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii(1.0), BorderWidths(1.0)))
            })

            addAll("C", Line(-100.0, -100.0, 100.0, 100.0).apply {
                strokeWidth = 1.0
                stroke = Color.GREEN
                strokeDashArray.addAll(5.0, 5.0)
            })
            addAll("A", Line(100.0, -100.0, -100.0, 100.0).apply {
                strokeWidth = 1.0
                stroke = Color.GREEN
                strokeDashArray.addAll(5.0, 5.0)
            })

        }
    }

    fun sampleContent(sharedEventLock: SharedEventLock): Group {
        return object : Group() {
            init {
                children.addAll(Pane().apply {
                    this.layoutX = 0.0
                    this.layoutY = 0.0
                    this.minWidth = 420.0
                    this.minHeight = 180.0
                    this.border = Border(BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, CornerRadii(1.0), BorderWidths(1.0)))
                })

                children.addAll(Pane().apply {
                    this.layoutX = 50.0
                    this.layoutY = 150.0
                    this.minWidth = 80.0
                    this.minHeight = 40.0
                    this.border = Border(BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii(1.0), BorderWidths(1.0)))
                })

                children.addAll(Line(-100.0, -100.0, 100.0, 100.0).apply {
                    strokeWidth = 1.0
                    stroke = Color.GREEN
                    strokeDashArray.addAll(5.0, 5.0)
                })
                children.addAll(Line(100.0, -100.0, -100.0, 100.0).apply {
                    strokeWidth = 1.0
                    stroke = Color.GREEN
                    strokeDashArray.addAll(5.0, 5.0)
                })

                children.addAll(ResizablePane(sharedEventLock))
            }
        }
    }
}