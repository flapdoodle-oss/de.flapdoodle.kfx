package de.flapdoodle.kfx.usecase.tab2.graph

import de.flapdoodle.kfx.controls.grapheditor.model.VertexContent
import de.flapdoodle.kfx.controls.grapheditor.model.VertexContentFactory
import javafx.scene.control.Button

object DummyVertexContentFactory : VertexContentFactory<String> {
  override fun vertexContent(value: String): VertexContent<String> {
    val node = Button(value)
    return VertexContent(node,node.textProperty())
  }
}