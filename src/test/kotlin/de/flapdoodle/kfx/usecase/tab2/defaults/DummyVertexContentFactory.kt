package de.flapdoodle.kfx.usecase.tab2.defaults

import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.model.VertexContent
import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.model.VertexContentFactory
import javafx.scene.control.Button

object DummyVertexContentFactory : VertexContentFactory<String> {
  override fun vertexContent(value: String): VertexContent<String> {
    val node = Button(value)
    return VertexContent(node,node.textProperty())
  }
}