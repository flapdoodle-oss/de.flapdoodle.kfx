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

val Region.size: Dimension2D
    get() = Dimension2D(width, height)

val Region.rawLayoutBounds: LayoutBounds
    get() = LayoutBounds(layoutPosition, size)

data class LayoutBounds(val layoutPosition: Point2D, val size: Dimension2D) {

    fun expand(direction: Direction, diff: Double): LayoutBounds {
        return when (direction) {
            RIGHT -> copy(size = size.addWidth(diff))
            LEFT -> copy(layoutPosition = layoutPosition.addX(diff), size = size.subWidth(diff))
            BOTTOM -> copy(size = size.addHeight(diff))
            TOP -> copy(layoutPosition = layoutPosition.addY(diff), size = size.subHeight(diff))
        }
    }
}