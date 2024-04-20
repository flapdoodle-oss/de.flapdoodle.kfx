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

import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

object Nodes {
    fun attach(base: Node, attachment: Node, position: Position, attachmentPosition: Position): AttachedNode {
        return AttachedNode(base, attachment, position, attachmentPosition)
    }

    fun attachBoundingBox(base: Node, rectangle: Rectangle): AttachedBoundingBox {
        return AttachedBoundingBox(base, rectangle)
    }

    fun boundingBox(): Rectangle {
        return Rectangle(10.0, 10.0, Color.rgb(255,255, 255, 0.3)).apply {
            strokeWidth = 1.0
            stroke = Color.RED
            strokeDashArray.addAll(5.0, 5.0)
            isMouseTransparent = true
        }
    }
}