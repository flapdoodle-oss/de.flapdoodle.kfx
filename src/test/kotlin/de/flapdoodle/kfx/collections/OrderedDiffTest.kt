package de.flapdoodle.kfx.collections

import de.flapdoodle.kfx.collections.OrderedDiff.between
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OrderedDiffTest {

  @Test
  fun addToList() {
    val changes = between(listOf(), listOf("1", "b")) { it }

    assertThat(changes)
      .containsExactly(
        OrderedDiff.Change.Add(0, "1"),
        OrderedDiff.Change.Add(1, "b")
      )
  }

  @Test
  fun removeFromList() {
    val changes = between(listOf("A", "b"), listOf("b")) { it }

    assertThat(changes)
      .containsExactly(
        OrderedDiff.Change.Remove(0)
      )
  }


  @Test
  fun removeAndAdd() {
    val changes = between(listOf("A", "b"), listOf("B", "C", "b")) { it }

    assertThat(changes)
      .containsExactly(
        OrderedDiff.Change.Remove(0),
        OrderedDiff.Change.Add(0, "B"),
        OrderedDiff.Change.Add(1, "C"),
        OrderedDiff.Change.Move(0, 2)
      )
  }

  @Test
  fun moveAndModify() {
    val changes = between(listOf("Aaa", "bee","Cee"), listOf("B", "Ccc", "bee")) { it.first() }

    assertThat(changes)
      .containsExactly(
        OrderedDiff.Change.Remove(0),
        OrderedDiff.Change.Add(0, "B"),
        OrderedDiff.Change.Modify(1, 1, "Ccc"),
        OrderedDiff.Change.Move(0, 2),
      )
  }
}