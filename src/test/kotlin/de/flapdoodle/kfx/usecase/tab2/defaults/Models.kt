package de.flapdoodle.kfx.usecase.tab2.defaults

import de.flapdoodle.kfx.controls.grapheditor.slots.Position
import de.flapdoodle.kfx.controls.grapheditor.slots.Slot
import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.model.Edge
import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.model.Model
import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.model.Vertex
import javafx.geometry.Point2D

object Models {
  fun emptyModel() = Model<String>()

  fun testModel(): Model<String> {
    val a = Vertex("a", "A", listOf(Slot("X-->", Slot.Mode.OUT, Position.RIGHT)), position = Point2D(10.0, 30.0))
    val b = Vertex("b", "B", listOf(Slot("-->X", Slot.Mode.IN, Position.LEFT)), position = Point2D(200.0, 90.0))
    return emptyModel()
      .add(a)
      .add(b)
      .add(Edge(a.id, a.slots[0].id, b.id, b.slots[0].id))
  }
}