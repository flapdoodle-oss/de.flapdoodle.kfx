/*
 * Copyright (C) 2022
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.kfx.controls.grapheditor.model

import de.flapdoodle.kfx.colors.HashedColors
import de.flapdoodle.kfx.controls.grapheditor.slots.Position
import de.flapdoodle.kfx.controls.grapheditor.slots.Slot
import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.model.Action
import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.model.Edge
import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.model.Model
import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.model.Vertex
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
    val vertex = Vertex("a", 1, listOf(Slot("x", Slot.Mode.IN, Position.LEFT, HashedColors.hashedColor("x"))))

    val actions = Action.syncActions(emptyModel(), emptyModel().add(vertex))

    assertThat(actions)
      .hasSize(2)
      .containsExactly(
        Action.AddVertex(vertex),
        Action.AddSlot(vertex.id, vertex.slots[0])
      )
  }

  @Test
  fun addVertexAndEdge() {
    val start = Vertex("a", 1, listOf(Slot("x", Slot.Mode.IN, Position.LEFT, HashedColors.hashedColor("x"))))
    val end = Vertex("b", 2, listOf(Slot("y", Slot.Mode.IN, Position.LEFT, HashedColors.hashedColor("y"))))
    val edge = Edge(start.id, start.slots[0].id, end.id, end.slots[0].id)

    val actions = Action.syncActions(emptyModel(), emptyModel().add(start, end).add(edge))

    assertThat(actions)
      .hasSize(5)
      .containsExactly(
        Action.AddVertex(start),
        Action.AddSlot(start.id, start.slots[0]),
        Action.AddVertex(end),
        Action.AddSlot(end.id, end.slots[0]),
        Action.AddEdge(edge),
      )
  }

  @Test
  fun removeVertex() {
    val vertex = Vertex("A", 1, listOf(Slot("x", Slot.Mode.IN, Position.LEFT, HashedColors.hashedColor("x"))))

    val actions = Action.syncActions(emptyModel().add(vertex), emptyModel())

    assertThat(actions)
      .hasSize(2)
      .containsExactly(
        Action.RemoveSlot(vertex.id, vertex.slots[0].id),
        Action.RemoveVertex(vertex.id)
      )
  }

  @Test
  fun removeVertexAndEdge() {
    val start = Vertex("a", 1, listOf(Slot("x", Slot.Mode.IN, Position.LEFT, HashedColors.hashedColor("a"))))
    val end = Vertex("b", 2, listOf(Slot("y", Slot.Mode.IN, Position.LEFT, HashedColors.hashedColor("b"))))
    val edge = Edge(start.id, start.slots[0].id, end.id, end.slots[0].id)

    val actions = Action.syncActions(emptyModel().add(start, end).add(edge), emptyModel())

    assertThat(actions)
      .hasSize(5)
      .containsExactly(
        Action.RemoveEdge(edge),
        Action.RemoveSlot(start.id, start.slots[0].id),
        Action.RemoveVertex(start.id),
        Action.RemoveSlot(end.id, end.slots[0].id),
        Action.RemoveVertex(end.id),
      )
  }
  @Test
  fun changeVertex() {
    val oldSlot = Slot("x", Slot.Mode.IN, Position.LEFT, HashedColors.hashedColor("x"))
    val newSlot = Slot("x", Slot.Mode.OUT, Position.RIGHT, HashedColors.hashedColor("x"))
    val stillThereSlot = Slot("x", Slot.Mode.OUT, Position.BOTTOM, HashedColors.hashedColor("x"))
    val unchangedSlot = Slot("x", Slot.Mode.IN, Position.LEFT, HashedColors.hashedColor("x"))

    val old = Vertex("A", 1, listOf(oldSlot, stillThereSlot, unchangedSlot))
    val new = old.copy(name = "B", data = 2, slots = listOf(unchangedSlot, newSlot, stillThereSlot.copy(name = "X")))

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