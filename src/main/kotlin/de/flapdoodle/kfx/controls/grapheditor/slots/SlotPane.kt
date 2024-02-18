package de.flapdoodle.kfx.controls.grapheditor.slots

import de.flapdoodle.kfx.bindings.and
import de.flapdoodle.kfx.controls.grapheditor.Markers
import de.flapdoodle.kfx.controls.grapheditor.Registry
import de.flapdoodle.kfx.controls.grapheditor.types.VertexId
import de.flapdoodle.kfx.controls.grapheditor.types.VertexSlotId
import de.flapdoodle.kfx.extensions.onAttach
import de.flapdoodle.kfx.extensions.plus
import de.flapdoodle.kfx.types.ColoredAngleAtPoint2D
import javafx.beans.value.ObservableValue
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.control.Tooltip
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.util.Duration
import javafx.util.Subscription

class SlotPane(
  registry: ObservableValue<Registry>,
  private val vertexId: VertexId,
  private val slot: Slot,
  position: Position
) : StackPane() {
  private val color = slot.color ?: if (slot.mode == Slot.Mode.IN) Color.GREEN else Color.RED
  private val circle = Circle(5.0, color).apply {
    Tooltip.install(this, Tooltip(slot.name))
  }
//  private val label = Label(slot.name)
  private var subscriptions = Subscription.EMPTY
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
    Markers.markAsNodeSlot(circle, VertexSlotId(vertexId, slot.id))
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

    onAttach {
      registry.value?.registerSlot(VertexSlotId(vertexId, slot.id), pointInSceneProperty)
    }.onDetach {
      registry.value?.unregisterSlot(VertexSlotId(vertexId, slot.id))
    }

    subscriptions += registry.subscribe { oldValue, newValue ->
      oldValue?.unregisterSlot(VertexSlotId(vertexId, slot.id))
      newValue?.registerSlot(VertexSlotId(vertexId, slot.id), pointInSceneProperty)
    }
  }

}

