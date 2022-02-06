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
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.kfx.layout.virtual.PanningWindow
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.stage.Stage

class PanningWindowsSampler : Application() {

    override fun start(stage: Stage) {
        val graphEditorProperties = BoxFactory.sampleProperties()

        val template = object : de.flapdoodle.kfx.clone.PanningWindow() {
            init {
                setEditorProperties(graphEditorProperties)
                setContent(sampleContent())
                WeightGridPane.setPosition(this, 0, 0)
            }
        }

        val testee = PanningWindow().apply {
            setContent(sampleContent())
            WeightGridPane.setPosition(this, 1, 0)
        }

        stage.scene = Scene(WeightGridPane().apply {
            children.add(template)
            children.add(testee)

            setColumnWeight(0, 1.0)
            setColumnWeight(1, 1.0)
            setRowWeight(0, 1.0)
        }, 800.0, 400.0)
        stage.show()
    }

    fun sampleContent(): Region {
        return object : Region() {
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

            }
        }
    }
}