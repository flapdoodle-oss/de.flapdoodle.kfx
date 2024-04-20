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
package de.flapdoodle.kfx.layout.virtual

import de.flapdoodle.kfx.extensions.layoutPosition
import de.flapdoodle.kfx.types.LayoutBounds
import javafx.geometry.Dimension2D
import javafx.scene.Node

data class Movable<T : Node>(
    val node: T,
    private val size: (T) -> Dimension2D,
    private val resizeable: ((T, Double, Double) -> Unit)? = null
) {
    fun size() = size(node)
    fun isResizeable() = (resizeable != null)
    fun rawLayoutBounds() = LayoutBounds(node.layoutPosition, size())

    fun resizeTo(width: Double, height: Double) {
        val asResizeable = resizeable
        require(asResizeable != null) { "node is not resizable: $node" }
        asResizeable(node, width, height)
    }
}