package de.flapdoodle.kfx.sampler

import de.flapdoodle.kfx.extensions.layoutPosition
import de.flapdoodle.kfx.extensions.withAnchors
import de.flapdoodle.kfx.nodeeditor.model.Slot
import de.flapdoodle.kfx.nodeeditor.Node
import de.flapdoodle.kfx.nodeeditor.NodeConnection
import de.flapdoodle.kfx.nodeeditor.NodeEditor
import de.flapdoodle.kfx.nodeeditor.model.Position
import de.flapdoodle.kfx.nodeeditor.types.NodeSlotId
import javafx.application.Application
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import javafx.stage.Stage

class NodeEditorSampler : Application() {
  override fun start(stage: Stage) {
    val wrapper = AnchorPane()
    val slotInA = Slot("a", Slot.Mode.IN, Position.LEFT, Color.DARKRED)
    val slotInB = Slot("b", Slot.Mode.IN, Position.LEFT, Color.DARKGREEN)
    val slotOutX = Slot("x", Slot.Mode.OUT, Position.RIGHT, Color.DARKBLUE)
    val slotOutY = Slot("y", Slot.Mode.OUT, Position.RIGHT)
    val slotOutZ = Slot("z", Slot.Mode.OUT, Position.RIGHT)
    val slotAgg1 = Slot("1", Slot.Mode.IN, Position.BOTTOM)
    val slotAgg2 = Slot("2", Slot.Mode.OUT, Position.BOTTOM)
    val slotAgg3 = Slot("3", Slot.Mode.IN, Position.BOTTOM)

    val nodeOne = Node("one").apply {
      layoutPosition = Point2D(100.0, 50.0)
      addConnector(slotInA)
      addConnector(slotOutX)
      addConnector(slotOutY)
      addConnector(slotOutZ)
    }
    val nodeTwo = Node("two").apply {
      val node = this
      content = Button("Helloooo").apply {
        onMouseClicked = EventHandler {
          println("clicked...")
          it.consume()
          node.toFront()
        }
      }
      addConnector(slotInA)
      addConnector(slotInB)
      addConnector(slotAgg1)
      addConnector(slotAgg2)
      addConnector(slotAgg3)
    }
    val node3 = Node("3").apply {
      val node = this
      layoutPosition = Point2D(200.0, 0.0)
      content = Button("Noop").apply {
        onMouseClicked = EventHandler {
          println("clicked...")
          it.consume()
          node.toFront()
        }
      }
      addConnector(slotInA)
      addConnector(slotOutX)
      addConnector(slotAgg1)
    }

    val nodeEditor = NodeEditor().withAnchors(all = 10.0)
    nodeEditor.layers().addNodes(nodeOne, nodeTwo, node3)
    nodeEditor.layers().addConnections(
      NodeConnection("one2two", NodeSlotId(nodeOne.nodeId, slotOutX.id), NodeSlotId(nodeTwo.nodeId,slotInA.id))
    )

    wrapper.children.add(nodeEditor)
    stage.scene = Scene(wrapper, 600.0, 400.0)
    stage.show()
  }
}