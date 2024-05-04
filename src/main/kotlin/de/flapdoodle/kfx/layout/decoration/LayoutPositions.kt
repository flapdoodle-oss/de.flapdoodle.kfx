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

import de.flapdoodle.kfx.bindings.ObjectBindings
import de.flapdoodle.kfx.extensions.minus
import de.flapdoodle.kfx.types.AngleAtPoint2D
import de.flapdoodle.kfx.types.BoundingBoxes
import de.flapdoodle.kfx.types.CardinalDirection
import javafx.geometry.Bounds
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.util.Subscription

object LayoutPositions {
  fun attachNodes(
    source: Node,
    sourceOffset: Offset,
    destination: Node,
    destinationOffset: Offset
  ): Subscription {
    val offset = ObjectBindings.merge(source.boundsInParentProperty(), destination.layoutBoundsProperty()) { sourceBounds , destinationBounds ->
      val ret = offset(sourceBounds, sourceOffset, destinationBounds, destinationOffset)
//      println("offset for $sourceBounds to $destinationBounds: $ret")
      ret
    }

    return offset.subscribe { it ->
      destination.layoutX = it.x
      destination.layoutY = it.y
    }
  }

  // VisibleForTest
  internal fun offset(
    sourceBounds: Bounds,
    sourceOffset: Offset,
    destinationBounds: Bounds,
    destinationOffset: Offset
  ): Point2D {
    val sourceOffsetStart = BoundingBoxes.pointAtEdge(sourceBounds, sourceOffset.direction)
    val sourceOffsetEnd = sourceOffsetStart.atDistance(sourceOffset.distance, 0.0, sourceOffset.offset)
    val destinationOffsetEnd = BoundingBoxes.pointAtEdge(destinationBounds, destinationOffset.direction)
    val destinationOffsetStart = destinationOffsetEnd.atDistance(destinationOffset.distance, 0.0, destinationOffset.offset)

    return sourceOffsetEnd.point2D - destinationOffsetStart.point2D
  }


  data class Offset(
    val direction: CardinalDirection,
    val distance: Double,
    val offset: Double = 0.0
  )
}