package de.flapdoodle.kfx.nodeeditor.connectors

import de.flapdoodle.kfx.bindings.and
import de.flapdoodle.kfx.nodeeditor.Markers
import de.flapdoodle.kfx.nodeeditor.NodeRegistry
import de.flapdoodle.kfx.nodeeditor.model.Position
import de.flapdoodle.kfx.nodeeditor.model.Slot
import de.flapdoodle.kfx.nodeeditor.types.NodeId
import de.flapdoodle.kfx.nodeeditor.types.NodeSlotId
import de.flapdoodle.kfx.types.AngleAtPoint2D
import de.flapdoodle.kfx.types.ColoredAngleAtPoint2D
import javafx.beans.value.ObservableValue
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Circle

class Connector(
  registry: ObservableValue<NodeRegistry>,
  private val nodeId: NodeId,
  private val slot: Slot,
  position: Position
) : StackPane() {
  private val color = slot.color ?: if (slot.mode == Slot.Mode.IN) Color.GREEN else Color.RED
  private val circle = Circle(5.0, color).apply {
    Tooltip.install(this, Tooltip(slot.name))
  }
//  private val label = Label(slot.name)
  private val space = Region()
  private val angle = when (position) {
    Position.LEFT -> 180.0
    Position.BOTTOM -> 90.0
    Position.RIGHT -> 0.0
  }

  private val pointInSceneProperty = circle.localToSceneTransformProperty().and(circle.radiusProperty()).map { transform, number ->
    ColoredAngleAtPoint2D(transform.transform(Point2D(0.0, 0.0)), angle, color)
  }

  init {
    Markers.markAsNodeSlot(circle, NodeSlotId(nodeId, slot.id))
    val wrapper = when (position) {
      Position.LEFT -> HBox().apply { alignment = Pos.CENTER }
      Position.RIGHT -> HBox().apply { alignment = Pos.CENTER }
      Position.BOTTOM -> VBox().apply { alignment = Pos.CENTER }
    }

    Tooltip.install(wrapper, Tooltip(slot.name))
    
    HBox.setHgrow(space, Priority.ALWAYS)
    VBox.setVgrow(space, Priority.ALWAYS)

    when (position) {
      Position.LEFT -> wrapper.children.addAll(circle, space)
      Position.RIGHT -> wrapper.children.addAll(space, circle)
      Position.BOTTOM -> wrapper.children.addAll(space, circle)
    }

    children.addAll(wrapper)

    registry.addListener { _, _, newValue ->
      newValue?.registerSlot(NodeSlotId(nodeId, slot.id), pointInSceneProperty)
    }
  }

}