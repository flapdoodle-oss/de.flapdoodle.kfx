package de.flapdoodle.kfx.layout.decoration

import de.flapdoodle.kfx.bindings.ObjectBindings
import de.flapdoodle.kfx.extensions.minus
import de.flapdoodle.kfx.types.AngleAtPoint2D
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
    val sourceOffsetStart = pointAtEdge(sourceBounds, sourceOffset.direction)
    val sourceOffsetEnd = sourceOffsetStart.atDistance(sourceOffset.distance, 0.0, sourceOffset.offset)
    val destinationOffsetEnd = pointAtEdge(destinationBounds, destinationOffset.direction)
    val destinationOffsetStart = destinationOffsetEnd.atDistance(destinationOffset.distance, 0.0, destinationOffset.offset)

    return sourceOffsetEnd.point2D - destinationOffsetStart.point2D
  }


  private fun pointAtEdge(bounds: Bounds, direction: CardinalDirection): AngleAtPoint2D {
    return when (direction) {
      CardinalDirection.NORTH -> AngleAtPoint2D(bounds.centerX, bounds.minY, -90.0)
      CardinalDirection.WEST -> AngleAtPoint2D(bounds.minX, bounds.centerY, 180.0)
      CardinalDirection.SOUTH -> AngleAtPoint2D(bounds.centerX, bounds.maxY, 90.0)
      CardinalDirection.EAST -> AngleAtPoint2D(bounds.maxX, bounds.centerY, 0.0)
      else -> throw IllegalArgumentException("not implemented: $direction")
    }
//    return when (direction) {
//      Base.LEFT -> Point2D(bounds.minX, bounds.maxY).to(Point2D(bounds.minX, bounds.minY))
//      Base.RIGHT -> Point2D(bounds.maxX, bounds.minY).to(Point2D(bounds.maxX, bounds.maxY))
//      Base.TOP -> Point2D(bounds.minX, bounds.minY).to(Point2D(bounds.maxX, bounds.minY))
//      Base.BOTTOM -> Point2D(bounds.maxX, bounds.maxY).to(Point2D(bounds.minX, bounds.maxY))
//      Base.HORIZONTAL -> Point2D(bounds.minX, bounds.centerY).to(Point2D(bounds.maxX, bounds.centerY))
//      Base.VERTICAL -> Point2D(bounds.centerX, bounds.minY).to(Point2D(bounds.centerX, bounds.maxY))
//    }
  }


  data class Offset(
    val direction: CardinalDirection,
    val distance: Double,
    val offset: Double = 0.0
  )
}