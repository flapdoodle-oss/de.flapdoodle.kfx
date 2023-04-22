package de.flapdoodle.kfx.sampler

import de.flapdoodle.kfx.extensions.layoutPosition
import de.flapdoodle.kfx.extensions.withAnchors
import de.flapdoodle.kfx.nodeeditor.model.Slot
import de.flapdoodle.kfx.nodeeditor.Node
import de.flapdoodle.kfx.nodeeditor.NodeConnection
import de.flapdoodle.kfx.nodeeditor.NodeEditor
import de.flapdoodle.kfx.nodeeditor.types.NodeSlotId
import javafx.application.Application
import javafx.geometry.Point2D
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage

class NodeEditorSampler : Application() {
  override fun start(stage: Stage) {
    val wrapper = AnchorPane()
    val slotIn = Slot("in", Slot.Mode.IN)
    val slotOut = Slot("1", Slot.Mode.OUT)

    val nodeOne = Node("one").apply {
      layoutPosition = Point2D(100.0, 50.0)
      addConnector(slotIn)
      addConnector(Slot("out", Slot.Mode.OUT))
      addConnector(slotOut)
      addConnector(Slot("2", Slot.Mode.OUT))
    }
    val nodeTwo = Node("two").apply {
      content = Button("Helloooo")
      addConnector(slotIn)
    }

    val nodeEditor = NodeEditor().withAnchors(all = 10.0)
    nodeEditor.layers().addNodes(nodeOne, nodeTwo)
    nodeEditor.layers().addConnections(
      NodeConnection("one2two", NodeSlotId(nodeOne.nodeId, slotOut.id), NodeSlotId(nodeTwo.nodeId,slotIn.id))
    )

    wrapper.children.add(nodeEditor)
    stage.scene = Scene(wrapper, 600.0, 400.0)
    stage.show()
  }
}