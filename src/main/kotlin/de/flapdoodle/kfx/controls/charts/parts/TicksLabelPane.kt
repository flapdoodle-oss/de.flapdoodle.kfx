package de.flapdoodle.kfx.controls.charts.parts

import de.flapdoodle.kfx.bindings.ObjectBindings
import de.flapdoodle.kfx.bindings.syncWith
import de.flapdoodle.kfx.controls.charts.ranges.Range
import de.flapdoodle.kfx.converters.ValidatingConverter
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.layout.StackLikeRegion
import de.flapdoodle.kfx.layout.decoration.Base
import de.flapdoodle.kfx.layout.decoration.Nodes
import de.flapdoodle.kfx.layout.decoration.Position
import de.flapdoodle.kfx.types.Direction
import de.flapdoodle.kfx.types.UnitInterval
import javafx.beans.value.ObservableValue
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.control.Label
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.text.Text

class TicksLabelPane<T : Any>(
  private val range: ObservableValue<Range<T>>,
  private val ticksWithLevel: ObservableValue<List<Pair<T, Int>>>,
  private val converter: ValidatingConverter<T>,
  private val direction: Direction
) : Pane() {

  val labels = ObjectBindings.merge(range, layoutBoundsProperty(), ticksWithLevel) { r, _, ticks ->
    val scaleLength = when (direction) {
      Direction.BOTTOM, Direction.TOP -> width - insets.left - insets.right
      Direction.LEFT, Direction.RIGHT -> height - insets.top - insets.bottom
    }

    val startOffset = when (direction) {
      Direction.BOTTOM, Direction.TOP -> insets.left
      Direction.LEFT, Direction.RIGHT -> insets.top
    }

    ticks.filter { it.second == 1 }
      .flatMap {
      val scaleOffset = r.offset(it.first, scaleLength) + startOffset
      val labelText = converter.toString(it.first)

      val baseNode = StackPane()
      val text = Label(labelText)
        
      Nodes.attach(
        base = baseNode,
        attachment = text,
        position = Position(Base.RIGHT, UnitInterval.ONE, 10.0),
        attachmentPosition = Position(Base.LEFT, UnitInterval.ONE, 10.0)
      )

      when (direction) {
        Direction.LEFT, Direction.RIGHT -> {
          baseNode.layoutX = 0.0
          baseNode.layoutY = scaleOffset
        }
        Direction.TOP, Direction.BOTTOM -> {
          baseNode.layoutY = 0.0
          baseNode.layoutX = scaleOffset
        }
      }

      listOf(baseNode, text)
    }
  }

  init {
    cssClassName("labels")

    children.syncWith(labels) { it }
  }
}