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
package de.flapdoodle.kfx.types

import de.flapdoodle.kfx.extensions.*
import de.flapdoodle.kfx.types.Direction.*
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.scene.layout.Region
import kotlin.math.sign

val Region.rawLayoutBounds: LayoutBounds
    get() = LayoutBounds(layoutPosition, size)

data class LayoutBounds constructor(val x: Double, val y: Double, val width: Double, val height: Double) {
    constructor(layoutPosition: Point2D, size: Dimension2D) : this(layoutPosition.x, layoutPosition.y, size.width, size.height)

    val layoutPosition = Point2D(x,y)
    val size = Dimension2D(width,height)

    fun expand(direction: Direction, diff: Double): LayoutBounds {
        return when (direction) {
            RIGHT -> copy(width = width + diff)
            LEFT -> copy(x = x + diff, width = width - diff)
            BOTTOM -> copy(height = height + diff)
            TOP -> copy(y = y + diff, height = height - diff)
        }
    }
}