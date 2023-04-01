package de.flapdoodle.kfx.sampler

import de.flapdoodle.kfx.extensions.withAnchors
import de.flapdoodle.kfx.nodeeditor.NodeEditor
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage

class NodeEditorSampler : Application() {
  override fun start(stage: Stage) {
    val wrapper = AnchorPane()
    wrapper.children.add(NodeEditor().withAnchors(all = 10.0))
    stage.scene = Scene(wrapper, 600.0, 400.0)
    stage.show()
  }
}