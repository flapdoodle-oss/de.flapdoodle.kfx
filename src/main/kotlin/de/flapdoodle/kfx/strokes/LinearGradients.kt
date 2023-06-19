package de.flapdoodle.kfx.strokes

import de.flapdoodle.kfx.bindings.ObjectBindings
import de.flapdoodle.kfx.extensions.Point2DMath
import de.flapdoodle.kfx.types.CardinalDirection
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
      val direction = Point2DMath.cardinalDirection(s, e)
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