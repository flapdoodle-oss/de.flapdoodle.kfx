package de.flapdoodle.kfx.nodeeditor.connectors

import de.flapdoodle.kfx.bindings.and
import de.flapdoodle.kfx.nodeeditor.Markers
import de.flapdoodle.kfx.nodeeditor.NodeRegistry
import de.flapdoodle.kfx.nodeeditor.model.Slot
import de.flapdoodle.kfx.nodeeditor.types.NodeId
import de.flapdoodle.kfx.nodeeditor.types.NodeSlotId
import de.flapdoodle.kfx.nodeeditor.types.SlotId
import de.flapdoodle.kfx.types.AngleAtPoint2D
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.geometry.Point2D
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.shape.Circle

class Connector(
  private val registry: ObservableValue<NodeRegistry>,
  private val nodeId: NodeId,
  val slot: Slot
) : HBox() {
  private val circle = Circle(4.0, Color.RED)
  private val pointInSceneProperty = circle.localToSceneTransformProperty().and(circle.radiusProperty()).map { transform, number ->
    AngleAtPoint2D(transform.transform(Point2D(0.0, 0.0)), if (slot.mode==Slot.Mode.OUT) 0.0 else 180.0)
  }

  init {
    Markers.markAsNodeSlot(circle, NodeSlotId(nodeId, slot.id))
    
    when (slot.mode) {
      Slot.Mode.IN -> children.addAll(circle, Label(slot.name))
      Slot.Mode.OUT -> children.addAll(Label(slot.name), circle)
    }

    if (false) {
      sceneProperty().addListener(ChangeListener { observable, oldValue, newValue ->
        println("$this: scene -> $oldValue -> $newValue")
      })

      localToSceneTransformProperty().addListener(ChangeListener { observable, oldValue, newValue ->
        val hasScene = this@Connector.scene != null
        println("$this ($hasScene): ${localToScene(Point2D(0.0, 0.0))}")
      })

      parentProperty().addListener(ChangeListener { observable, oldValue, newValue ->
        val hasScene = this@Connector.scene != null
        println("$this ($hasScene): parent has changed somehow")
      })
    }

    registry.addListener(ChangeListener { observable, oldValue, newValue ->
      newValue?.registerSlot(NodeSlotId(nodeId, slot.id), pointInSceneProperty)
    })
  }

}