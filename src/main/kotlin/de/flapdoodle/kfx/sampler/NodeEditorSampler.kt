package de.flapdoodle.kfx.sampler

import de.flapdoodle.kfx.extensions.layoutPosition
import de.flapdoodle.kfx.extensions.withAnchors
import de.flapdoodle.kfx.nodeeditor.Node
import de.flapdoodle.kfx.nodeeditor.NodeEditor
import javafx.application.Application
import javafx.geometry.Point2D
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.Stage

class NodeEditorSampler : Application() {
  override fun start(stage: Stage) {
    val wrapper = AnchorPane()
    wrapper.children.add(NodeEditor().withAnchors(all = 10.0).apply {
      layers().nodes().children.addAll(Node("one").apply {
        layoutPosition = Point2D(100.0, 50.0)
      }, Node("two").apply {
        content = Button("Helloooo")
      })

      layers().connections().children.addAll(
        Rectangle(40.0, 30.0, Color.BISQUE)
      )
    })
    stage.scene = Scene(wrapper, 600.0, 400.0)
    stage.show()
  }
}