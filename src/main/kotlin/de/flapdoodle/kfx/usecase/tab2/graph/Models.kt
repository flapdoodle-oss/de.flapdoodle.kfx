package de.flapdoodle.kfx.usecase.tab2.graph

import de.flapdoodle.kfx.controls.grapheditor.model.Model
import de.flapdoodle.kfx.controls.grapheditor.model.Vertex

object Models {
  fun emptyModel() = Model<String>()

  fun testModel() = emptyModel()
    .add(Vertex("a","A"))
    .add(Vertex("b", "B"))
}