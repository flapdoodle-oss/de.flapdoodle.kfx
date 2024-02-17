package de.flapdoodle.kfx.usecase.tab2.graph.actions

import de.flapdoodle.kfx.controls.grapheditor.model.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ActionTest {

  @Test
  fun noChange() {
    assertThat(Action.syncActions(emptyModel(), emptyModel()))
      .isEmpty()
  }

  @Test
  fun addVertex() {
    val vertex = Vertex("a", 1, listOf(Slot("x", Slot.Mode.IN, Position.LEFT)))
    
    val actions = Action.syncActions(emptyModel(), emptyModel().add(vertex))
    
    assertThat(actions)
      .hasSize(2)
      .containsExactly(
        Action.AddVertex(vertex),
        Action.AddSlot(vertex.id, vertex.slots[0])
      )
  }

  @Test
  fun removeVertex() {
    val vertex = Vertex("A", 1, listOf(Slot("x", Slot.Mode.IN, Position.LEFT)))

    val actions = Action.syncActions(emptyModel().add(vertex), emptyModel())

    assertThat(actions)
      .hasSize(2)
      .containsExactly(
        Action.RemoveSlot(vertex.id, vertex.slots[0].id),
        Action.RemoveVertex(vertex.id)
      )
  }

  @Test
  fun changeVertex() {
    val oldSlot = Slot("x", Slot.Mode.IN, Position.LEFT)
    val newSlot = Slot("x", Slot.Mode.OUT, Position.RIGHT)
    val stillThereSlot = Slot("x", Slot.Mode.OUT, Position.BOTTOM)
    val unchangedSlot = Slot("x", Slot.Mode.IN, Position.LEFT)

    val old = Vertex("A", 1, listOf(oldSlot, stillThereSlot, unchangedSlot))
    val new = Vertex("B", 2, listOf(unchangedSlot, newSlot, stillThereSlot.copy(name = "X")), old.id)

    val actions = Action.syncActions(emptyModel().add(old), emptyModel().add(new))

    assertThat(actions)
      .hasSize(4)
      .containsExactly(
        Action.RemoveSlot(old.id, oldSlot.id),
        Action.ChangeVertex(old.id, new),
        Action.ChangeSlot(new.id, stillThereSlot.id, stillThereSlot.copy(name = "X")),
        Action.AddSlot(old.id, newSlot)
      )
  }

  private fun emptyModel() = Model<Int>()
}