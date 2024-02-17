package de.flapdoodle.kfx.usecase.tab2.graph.model

import de.flapdoodle.kfx.controls.grapheditor.model.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ModelTest {

  @Test
  fun useCase() {
    var testee = Model<Int>()
    val x = Slot("x", Slot.Mode.IN, Position.LEFT)
    val y = Slot("y", Slot.Mode.OUT, Position.RIGHT)
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

    assertThat(testee.edgeList)
      .hasSize(1)
      .containsExactly(edge)
  }
}