package de.flapdoodle.kfx.sampler

import de.flapdoodle.kfx.extensions.layoutPosition
import de.flapdoodle.kfx.extensions.withAnchors
import de.flapdoodle.kfx.nodeeditor.Node
import de.flapdoodle.kfx.nodeeditor.NodeConnection
import de.flapdoodle.kfx.nodeeditor.NodeEditor
import javafx.application.Application
import javafx.geometry.Point2D
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage

class NodeEditorSampler : Application() {
  override fun start(stage: Stage) {
    val wrapper = AnchorPane()
    val nodeOne = Node("one").apply {
      layoutPosition = Point2D(100.0, 50.0)
    }
    val nodeTwo = Node("two").apply {
      content = Button("Helloooo")
    }

    val nodeEditor = NodeEditor().withAnchors(all = 10.0)
    nodeEditor.layers().nodes().add(nodeOne, nodeTwo)
    nodeEditor.layers().connections().add(
      NodeConnection("one2two", nodeOne.uuid, nodeTwo.uuid)
    )

    wrapper.children.add(nodeEditor)
    stage.scene = Scene(wrapper, 600.0, 400.0)
    stage.show()
  }
}