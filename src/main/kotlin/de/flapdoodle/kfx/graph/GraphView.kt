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
package de.flapdoodle.kfx.graph

import de.flapdoodle.kfx.events.SharedEventLock
import de.flapdoodle.kfx.extensions.size
import de.flapdoodle.kfx.graph.connections.Connections
import de.flapdoodle.kfx.graph.nodes.Movable
import de.flapdoodle.kfx.graph.nodes.Movables
import de.flapdoodle.kfx.layout.layer.LayerPane
import de.flapdoodle.kfx.layout.virtual.PanZoomPanel
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class GraphView : Region() {
    // model: Graph
    // components: ...
    enum class Layer {
        Nodes,
        Connections
    }

    init {
        val sharedEventLock = SharedEventLock()
        val nodes = Movables(sharedEventLock) { node ->
            when (node) {
                is Rectangle -> Movable(node, Rectangle::size) { it, w, h -> it.width = w;it.height = h }
                else -> null
            }
        }
        nodes.addAll(Rectangle(30.0,30.0).apply {
            fill = Color.RED
        })
        val layers = LayerPane(setOf(*Layer.values()))
        layers.addAll(Layer.Nodes, nodes)
        val connections = Connections(sharedEventLock)
        
        layers.addAll(Layer.Connections, connections)

        children.add(PanZoomPanel().apply {
            setContent(layers)
        })
    }

    override fun layoutChildren() {
        val managed = getManagedChildren<Node>()

        val width = width
        val height = height

        val top = insets.top
        val right = insets.right
        val left = insets.left
        val bottom = insets.bottom

        val contentWidth = width - left - right
        val contentHeight = height - top - bottom

        managed.forEach { child ->
            layoutInArea(
                child, left, top, contentWidth, contentHeight,
                0.0, null, HPos.CENTER, VPos.CENTER
            )
        }
    }
}