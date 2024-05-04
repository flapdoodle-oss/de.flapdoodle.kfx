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
package de.flapdoodle.kfx.usecase.tab2.graph.model

import de.flapdoodle.kfx.colors.HashedColors
import de.flapdoodle.kfx.controls.grapheditor.slots.Position
import de.flapdoodle.kfx.controls.grapheditor.slots.Slot
import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.model.Edge
import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.model.Model
import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.model.Vertex
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ModelTest {

  @Test
  fun useCase() {
    var testee = Model<Int>()
    val x = Slot("x", Slot.Mode.IN, Position.LEFT, HashedColors.hashedColor("x"))
    val y = Slot("y", Slot.Mode.OUT, Position.RIGHT, HashedColors.hashedColor("y"))
    val a = Vertex("A", 1, slots = listOf(x))
    val b = Vertex("B", 2, slots = listOf(y))

    testee = testee.add(a)

    assertThat(testee.vertexList)
      .hasSize(1)
      .containsExactly(a)

    testee = testee.add(b)

    assertThat(testee.vertexList)
      .hasSize(2)
      .containsExactly(a, b)

    val edge = Edge(a.id, x.id, b.id, y.id)

    testee = testee.add(edge)

    assertThat(testee.edgeSet)
      .containsExactly(edge)
      .hasSize(1)
  }
}