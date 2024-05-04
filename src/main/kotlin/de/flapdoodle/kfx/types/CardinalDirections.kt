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
package de.flapdoodle.kfx.types

object CardinalDirections {
  private data class CardinalDirectionAngleRange(val start: Double, val end: Double, val direction: CardinalDirection)

  private val segments = CardinalDirection.values().size
  private val segmentAngle = 360.0 / segments
  private fun angleDirection(angle: Double, direction: CardinalDirection): CardinalDirectionAngleRange {
    return CardinalDirectionAngleRange(angle - (segmentAngle / 2.0), angle + (segmentAngle / 2.0), direction)
  }

  private val directionMapping = listOf<CardinalDirectionAngleRange>(
    angleDirection(0.0, CardinalDirection.EAST),
    angleDirection(45.0, CardinalDirection.NORTHEAST),
    angleDirection(90.0, CardinalDirection.NORTH),
    angleDirection(135.0, CardinalDirection.NORTHWEST),
  )

  private data class CardinalDirectionOffset(val index: Int, val direction: CardinalDirection)

  private val offsetMap = listOf<CardinalDirectionOffset>(
    CardinalDirectionOffset(0, CardinalDirection.EAST),
    CardinalDirectionOffset(1, CardinalDirection.NORTHEAST),
    CardinalDirectionOffset(2, CardinalDirection.NORTH),
    CardinalDirectionOffset(3, CardinalDirection.NORTHWEST),
    CardinalDirectionOffset(4, CardinalDirection.WEST),
    CardinalDirectionOffset(5, CardinalDirection.SOUTHWEST),
    CardinalDirectionOffset(-1, CardinalDirection.SOUTHEAST),
    CardinalDirectionOffset(-2, CardinalDirection.SOUTH),
    CardinalDirectionOffset(-3, CardinalDirection.SOUTHWEST),
    CardinalDirectionOffset(-4, CardinalDirection.WEST),
  )

  internal fun cardinalDirection(angle: Double): CardinalDirection {
    var angleInRange = angle.mod(360.0)
    if (angleInRange > 180.0) angleInRange -= 360.0
    val offset = if (angleInRange>=0) {
       ((angleInRange + segmentAngle / 2.0) / segmentAngle).toInt()
    } else {
      ((angleInRange - segmentAngle / 2.0) / segmentAngle).toInt()
    }
    return offsetMap.first { it.index == offset }.direction
  }
}