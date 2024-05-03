package de.flapdoodle.kfx.controls.charts.parts

import com.sun.javafx.scene.layout.ScaledMath
import de.flapdoodle.kfx.bindings.syncWith
import de.flapdoodle.kfx.controls.charts.ranges.Range
import de.flapdoodle.kfx.controls.textfields.ValidatedLabel
import de.flapdoodle.kfx.converters.ValidatingConverter
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.extensions.minus
import de.flapdoodle.kfx.types.BoundingBoxes
import de.flapdoodle.kfx.types.CardinalDirection
import de.flapdoodle.kfx.types.Direction
import javafx.beans.value.ObservableValue
import javafx.geometry.*
import javafx.scene.layout.Pane
import kotlin.math.min

class TicksLabelPane<T : Any>(
  private val range: ObservableValue<Range<T>>,
  private val ticksWithLevel: ObservableValue<List<Pair<T, Int>>>,
  private val converter: ValidatingConverter<T>,
  private val direction: Direction
) : Pane() {

  init {
    cssClassName("labels")

    children.syncWith(ticksWithLevel.map { list -> list.filter { it.second == 2 }.map { it.first } }) {
      TickLabel(converter, it)
    }
  }

  override fun computePrefHeight(width: Double): Double {
    return children.maxOfOrNull { it.prefHeight(width) } ?: (0.0 + insets.top + insets.bottom)
  }

  override fun computePrefWidth(height: Double): Double {
    return children.maxOfOrNull { it.prefWidth(width) } ?: (0.0 + insets.left + insets.right)
  }

  override fun layoutChildren() {
    val scaleLength = when (direction) {
      Direction.BOTTOM, Direction.TOP -> width - insets.left - insets.right
      Direction.LEFT, Direction.RIGHT -> height - insets.top - insets.bottom
    }

    val startOffset = when (direction) {
      Direction.BOTTOM, Direction.TOP -> insets.left
      Direction.LEFT, Direction.RIGHT -> insets.top
    }

    val r = range.value

    var label2position = emptyList<TickedLabelAndBounds<T>>()

    children.forEach { node ->
      if (node.isManaged) {
        if (node is TickLabel<out Any>) {
          val tickedLabel = node as TickLabel<T>
          val scaleOffset = r.offset(tickedLabel.tick, scaleLength) + startOffset

          tickedLabel.autosize()
          val ticketLabelBounds = tickedLabel.layoutBounds

          val prefWidth = ticketLabelBounds.width //  tickedLabel.prefWidth(-1.0)
          val prefHeight = ticketLabelBounds.height // tickedLabel.prefHeight(-1.0)

          val scalePoint = when (direction) {
            Direction.LEFT -> Point2D(width - insets.right - 1.0, scaleOffset)
            Direction.RIGHT -> Point2D(insets.left, scaleOffset)
            Direction.TOP -> Point2D(scaleOffset, height - insets.bottom - 1.0)
            Direction.BOTTOM -> Point2D(scaleOffset, insets.top)
          }
          if (direction == Direction.TOP) {
//            println("--> $scalePoint (h: $height, inset.b: ${insets.bottom})")
          }

          val w = snappedToPixel(min(prefWidth, width - insets.left - insets.right))
          val h = snappedToPixel(min(prefHeight, height - insets.top - insets.bottom))

          val delta = BoundingBoxes.pointAtEdge(
            ticketLabelBounds, when (direction) {
              Direction.LEFT -> CardinalDirection.EAST
              Direction.RIGHT -> CardinalDirection.WEST
              Direction.TOP -> CardinalDirection.SOUTH
              Direction.BOTTOM -> CardinalDirection.NORTH
            }
          ).point2D

          if (direction == Direction.TOP) {
//            println("--> $scalePoint (h: $height, inset.b: ${insets.bottom})")
          }

          val pos = scalePoint.minus(delta)

          val x = pos.x
          val y = pos.y

          label2position = label2position + (TickedLabelAndBounds(tickedLabel, BoundingBox(x, y, w, h)))

          layoutInArea(tickedLabel, x, y, w, h, -1.0, HPos.LEFT, VPos.TOP)
        } else {
          if (node.isResizable) {
            node.autosize()
          }
        }
      }
    }

    val sorted = label2position.sortedBy { it.bounds.minX }.sortedBy { it.bounds.minY }

    val labelsWithoutCollisions = filterUntilNoCollisions(sorted).map { it.tickLabel }.toSet()

    sorted.forEach {
      it.tickLabel.isVisible = labelsWithoutCollisions.contains(it.tickLabel)
    }
  }

  private fun filterUntilNoCollisions(list: List<TickedLabelAndBounds<T>>): List<TickedLabelAndBounds<T>> {
    val odd = list.filterIndexed { index, pair -> index % 2 == 0 }
    val even = list.filterIndexed { index, pair -> index % 2 == 1  }
    val collides = odd.zip(even).any { (e, o) -> e.bounds.intersects(o.bounds) }
    return if (collides) {
       filterUntilNoCollisions(odd)
    } else {
      list
    }
  }

  private fun snappedToPixel(value: Double): Double {
    return if (isSnapToPixel) ScaledMath.ceil(value, 1.0) else value
  }

  data class TickedLabelAndBounds<T: Any>(
    val tickLabel: TickLabel<T>,
    val bounds: Bounds
  )

  class TickLabel<T : Any>(
    converter: ValidatingConverter<T>,
    val tick: T,
  ) : ValidatedLabel<T>(converter) {
    init {
      set(tick)
    }
  }
}