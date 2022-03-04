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
package de.flapdoodle.kfx.graph.nodes

import de.flapdoodle.kfx.types.Direction
import de.flapdoodle.kfx.types.LayoutBounds
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.Cursor.*

enum class SizeMode(private val cursor: Cursor) {
    NORTH(N_RESIZE),
    NORTHEAST(NE_RESIZE),
    EAST(E_RESIZE),
    SOUTHEAST(SE_RESIZE),
    SOUTH(S_RESIZE),
    SOUTHWEST(SW_RESIZE),
    WEST(W_RESIZE),
    NORTHWEST(NW_RESIZE),
    INSIDE(MOVE);

    fun cursor() = cursor

    companion object {
        fun guess(position: Point2D, size: Dimension2D): SizeMode? {
            return guess(position.x, position.y, size.width, size.height)
        }
        
        private fun guess(
            x: Double,
            y: Double,
            width: Double,
            height: Double,
            DEFAULT_RESIZE_BORDER_TOLERANCE: Double = 8.0
        ): SizeMode? {
            if (x < 0 || y < 0 || x > width || y > height) {
                return null
            }
            val isNorth = y < DEFAULT_RESIZE_BORDER_TOLERANCE
            val isSouth = y > height - DEFAULT_RESIZE_BORDER_TOLERANCE
            val isEast = x > width - DEFAULT_RESIZE_BORDER_TOLERANCE
            val isWest = x < DEFAULT_RESIZE_BORDER_TOLERANCE

            return if (isNorth && isEast) {
                NORTHEAST
            } else if (isNorth && isWest) {
                NORTHWEST
            } else if (isSouth && isEast) {
                SOUTHEAST
            } else if (isSouth && isWest) {
                SOUTHWEST
            } else if (isNorth) {
                NORTH
            } else if (isSouth) {
                SOUTH
            } else if (isEast) {
                EAST
            } else if (isWest) {
                WEST
            } else {
                INSIDE
            }
        }

        fun resize(sizeMode: SizeMode, base: LayoutBounds, diff: Point2D): LayoutBounds {
            return when(sizeMode) {
                NORTH -> base.expand(Direction.TOP, diff.y)
                NORTHEAST -> base.expand(Direction.TOP, diff.y).expand(Direction.RIGHT, diff.x)
                EAST -> base.expand(Direction.RIGHT, diff.x)
                SOUTHEAST -> base.expand(Direction.RIGHT, diff.x).expand(Direction.BOTTOM, diff.y)
                SOUTH -> base.expand(Direction.BOTTOM, diff.y)
                SOUTHWEST -> base.expand(Direction.BOTTOM, diff.y).expand(Direction.LEFT, diff.x)
                WEST -> base.expand(Direction.LEFT, diff.x)
                NORTHWEST -> base.expand(Direction.LEFT, diff.x).expand(Direction.TOP, diff.y)
                else -> base
            }
        }
    }
}