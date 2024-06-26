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

import de.flapdoodle.kfx.extensions.minus
import de.flapdoodle.kfx.types.Line2D
import de.flapdoodle.kfx.types.to
import javafx.beans.value.ChangeListener
import javafx.geometry.Bounds
import javafx.geometry.Point2D
import javafx.scene.Node

class AttachedNode(
    base: Node,
    private val attachment: Node,
    private val position: Position,
    private val attachmentPosition: Position
) {
    private val changeListener = ChangeListener<Bounds> { observable, old, it ->
        val result = offset(it, position, attachment.boundsInLocal, attachmentPosition)
        attachment.relocate(result.x, result.y)
    }

    init {
        base.boundsInParentProperty().addListener(changeListener)
    }

    companion object {

        private fun borderLine(bounds: Bounds, direction: Base): Line2D {
            return when (direction) {
                Base.LEFT -> Point2D(bounds.minX, bounds.maxY).to(Point2D(bounds.minX, bounds.minY))
                Base.RIGHT -> Point2D(bounds.maxX, bounds.minY).to(Point2D(bounds.maxX, bounds.maxY))
                Base.TOP -> Point2D(bounds.minX, bounds.minY).to(Point2D(bounds.maxX, bounds.minY))
                Base.BOTTOM -> Point2D(bounds.maxX, bounds.maxY).to(Point2D(bounds.minX, bounds.maxY))
                Base.HORIZONTAL -> Point2D(bounds.minX, bounds.centerY).to(Point2D(bounds.maxX, bounds.centerY))
                Base.VERTICAL -> Point2D(bounds.centerX, bounds.minY).to(Point2D(bounds.centerX, bounds.maxY))
            }
        }

        internal fun offset(
            sourceBounds: Bounds,
            sourcePosition: Position,
            attachmentBounds: Bounds,
            attachmentPosition: Position
        ): Point2D {
            val line = borderLine(sourceBounds, sourcePosition.base)
            val position = line.positionAt(sourcePosition.position, sourcePosition.distance, sourcePosition.offset)

            val bounds = borderLine(attachmentBounds, attachmentPosition.base)
            val attPosition = bounds.positionAt(attachmentPosition.position, attachmentPosition.distance, attachmentPosition.offset)

            return position.minus(attPosition)
        }
    }
}