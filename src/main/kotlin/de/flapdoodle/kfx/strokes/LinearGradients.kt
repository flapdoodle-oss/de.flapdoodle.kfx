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
package de.flapdoodle.kfx.strokes

import de.flapdoodle.kfx.bindings.ObjectBindings
import de.flapdoodle.kfx.types.Point2DMath.angle
import de.flapdoodle.kfx.types.CardinalDirection
import de.flapdoodle.kfx.types.CardinalDirections
import javafx.beans.value.ObservableValue
import javafx.geometry.Point2D
import javafx.scene.paint.*

object LinearGradients {

  private data class GradientKey(val direction: CardinalDirection, val start: Color, val end: Color)
  private data class KeyedGradient(val key: GradientKey, val gradient: LinearGradient)
  private data class GradientCoord(val startX: Double, val startY: Double, val endX: Double, val endY: Double)

  private val coordMapping = mapOf(
    CardinalDirection.NORTH to GradientCoord(0.0, 0.0, 0.0, 1.0),
    CardinalDirection.NORTHEAST to GradientCoord(0.0, 0.0, 1.0, 1.0),
    CardinalDirection.EAST to GradientCoord(0.0, 0.0, 1.0, 0.0),
    CardinalDirection.SOUTHEAST to GradientCoord(0.0, 1.0, 1.0, 0.0),
    CardinalDirection.SOUTH to GradientCoord(0.0, 1.0, 0.0, 0.0),
    CardinalDirection.SOUTHWEST to GradientCoord(1.0, 1.0, 0.0, 0.0),
    CardinalDirection.WEST to GradientCoord(1.0, 0.0, 0.0, 0.0),
    CardinalDirection.NORTHWEST to GradientCoord(1.0, 0.0, 0.0, 1.0),
  )

  private fun gradient(direction: CardinalDirection, start: Color, end: Color): LinearGradient {
    val coord = coordMapping[direction] ?: throw IllegalArgumentException("no mapping for $direction")

    return LinearGradient(
      coord.startX, coord.startY, coord.endX, coord.endY, true, CycleMethod.NO_CYCLE,
      Stop(0.0, start),
      Stop(1.0, end)
    )
  }

  fun cardinal(
    start: ObservableValue<Point2D>,
    end: ObservableValue<Point2D>,
    startColor: ObservableValue<Color>,
    endColor: ObservableValue<Color>
  ): ObservableValue<Paint> {

    var cached = KeyedGradient(
      GradientKey(CardinalDirection.EAST, Color.BLACK, Color.BLACK),
      gradient(CardinalDirection.EAST, Color.BLACK, Color.BLACK)
    )

    return ObjectBindings.merge(start, end, startColor, endColor) { s, e, colorAtStart, colorAtEnd ->
      val direction = CardinalDirections.cardinalDirection(angle(s, e))
      val key = GradientKey(direction, colorAtStart, colorAtEnd)
      if (cached.key != key) {
        cached = KeyedGradient(key, gradient(key.direction, key.start, key.end))
      }

      cached.gradient
    }
  }

  fun exact(
    start: ObservableValue<Point2D>,
            end: ObservableValue<Point2D>,
            startColor: ObservableValue<Color>,
            endColor: ObservableValue<Color>
  ): ObservableValue<Paint> {
    return ObjectBindings.merge(start, end, startColor, endColor) { s, e, colorAtStart, colorAtEnd ->
      LinearGradient(
        s.x, s.y, e.x, e.y,false,
        CycleMethod.NO_CYCLE,
        Stop(0.0, colorAtStart),
        Stop(1.0, colorAtEnd)
      )
    }
  }
}