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
package de.flapdoodle.kfx.layout.decoration

import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.geometry.Bounds
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.shape.Rectangle

class AttachedBoundingBox(
    node: Node,
    rectangle: Rectangle
) : Group() {

    private val changeListener = ChangeListener<Bounds> { _, _, bounds ->
        resize(rectangle, bounds)
    }

    private fun resize(rectangle: Rectangle, bounds: Bounds) {
        rectangle.layoutX = bounds.minX
        rectangle.layoutY = bounds.minY
        rectangle.width = bounds.width
        rectangle.height = bounds.height
    }

    init {
        node.boundsInParentProperty().addListener(changeListener)
        node.boundsInParentProperty().addListener(InvalidationListener {
            resize(rectangle, node.boundsInParent)
        })
    }

    companion object {
        fun attach(node: Node, rectangle: Rectangle): AttachedBoundingBox {
            return AttachedBoundingBox(node, rectangle)
        }
    }
}