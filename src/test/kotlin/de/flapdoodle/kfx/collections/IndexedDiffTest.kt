package de.flapdoodle.kfx.collections

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class IndexedDiffTest {

  @Test
  fun removeFromList() {
    val changes = IndexedDiff.changes(listOf("A", "b", "A"), listOf("b"))

    assertThat(changes)
      .containsExactly(
        IndexedDiff.Change.Remove(0),
        IndexedDiff.Change.Remove(2)
      )
  }

  @Test
  fun moveAndMultiply() {
    val changes = IndexedDiff.changes(listOf("A", "b", "c", "A"), listOf("b", "A", "c", "A", "A"))

    assertThat(changes)
      .containsExactly(
        IndexedDiff.Change.Move(0, 1),
        IndexedDiff.Change.Move(1, 0),
        IndexedDiff.Change.Add(4, "A")
      )
  }

  @Test
  fun moveAndRemove() {
    val changes = IndexedDiff.changes(listOf("A", "b", "c", "A", "A"), listOf("b", "A", "c", "A"))

    assertThat(changes)
      .containsExactly(
        IndexedDiff.Change.Move(0, 1),
        IndexedDiff.Change.Move(1, 0),
        IndexedDiff.Change.Remove(4)
      )
  }
}