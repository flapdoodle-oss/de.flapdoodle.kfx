package de.flapdoodle.kfx.extensions

import de.flapdoodle.kfx.types.CardinalDirection
import javafx.geometry.Point2D

object Point2DMath {
  fun angle(p1: Point2D, p2: Point2D): Double {
    val xDiff = p2.x - p1.x
    val yDiff = p2.y - p1.y
    return Math.toDegrees(Math.atan2(yDiff, xDiff))
  }

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

  fun cardinalDirection(p1: Point2D, p2: Point2D): CardinalDirection {
    val angle = angle(p1, p2)
    return CardinalDirections.cardinalDirection(angle)
  }

}