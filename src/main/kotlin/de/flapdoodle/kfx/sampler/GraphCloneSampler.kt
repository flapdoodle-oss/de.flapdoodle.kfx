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

import de.flapdoodle.kfx.clone.AutoScrollingWindow
import de.flapdoodle.kfx.clone.BoxFactory
import de.flapdoodle.kfx.clone.GraphEditorView
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage

class GraphCloneSampler : Application() {

    override fun start(stage: Stage) {
        val graphEditorProperties = BoxFactory.sampleProperties()
        val graphEditorView = GraphEditorView(graphEditorProperties)
        graphEditorView.add(BoxFactory.sampleBox(graphEditorProperties))

        val scrollPane = object : AutoScrollingWindow() {
            init {
                setContent(graphEditorView)
                setEditorProperties(graphEditorProperties)
                graphEditorView.resize(800.0, 800.0)
                checkWindowBounds()
            }
        }
        stage.scene = Scene(scrollPane, 800.0, 800.0)
        stage.scene.stylesheets.add(GraphEditorView.getStyleResource())
        stage.show()
    }
}