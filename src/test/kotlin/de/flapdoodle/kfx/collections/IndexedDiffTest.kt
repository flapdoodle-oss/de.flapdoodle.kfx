package de.flapdoodle.kfx.collections

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class IndexedDiffTest {

  @Test
  fun addToList() {
    val changes = IndexedDiff.changes(listOf(), listOf("1", "b"))

    assertThat(changes)
      .containsExactly(
        IndexedDiff.Change.Add(0, "1"),
        IndexedDiff.Change.Add(1, "b")
      )
  }

  @Test
  fun removeFromList() {
    val changes = IndexedDiff.changes(listOf("A", "b", "A"), listOf("b"))

    assertThat(changes)
      .containsExactly(
        IndexedDiff.Change.Remove(0),
        IndexedDiff.Change.Remove(2),
        IndexedDiff.Change.Move(0,0)
      )
  }

  @Test
  fun removeAndAdd() {
    val changes = IndexedDiff.changes(listOf("A", "b", "A"), listOf("B", "b", "C"))

    assertThat(changes)
      .containsExactly(
        IndexedDiff.Change.Remove(0),
        IndexedDiff.Change.Remove(2),
        IndexedDiff.Change.Add(0, "B"),
        IndexedDiff.Change.Move(0, 1),
        IndexedDiff.Change.Add(2, "C")

      )
  }

  @Test
  fun moveAndMultiply() {
    val changes = IndexedDiff.changes(listOf("A", "b", "c", "A"), listOf("b", "A", "c", "A", "A"))

    assertThat(changes)
      .containsExactly(
        IndexedDiff.Change.Move(1, 0),
        IndexedDiff.Change.Move(0, 1),
        IndexedDiff.Change.Move(2, 2),
        IndexedDiff.Change.Move(3, 3),
        IndexedDiff.Change.Add(4, "A")
      )
  }

  @Test
  fun moveAndRemove() {
    val changes = IndexedDiff.changes(listOf("A", "b", "c", "A", "A"), listOf("b", "A", "c", "A"))

    assertThat(changes)
      .containsExactly(
        IndexedDiff.Change.Move(1, 0),
        IndexedDiff.Change.Move(0, 1),
        IndexedDiff.Change.Move(2, 2),
        IndexedDiff.Change.Move(3, 3),
        IndexedDiff.Change.Remove(4)
      )
  }

  @Test
  fun sample() {
    val changes = IndexedDiff.changes(
      listOf("1", "2", "3", "a", "a", "4", "5"),
      listOf("1", "a", "2", "4", "2", "b")
    )

    assertThat(changes)
      .containsExactly(
        IndexedDiff.Change.Remove(2),
        IndexedDiff.Change.Remove(6),
        IndexedDiff.Change.Move(0, 0),
        IndexedDiff.Change.Move(2, 1),
        IndexedDiff.Change.Move(1, 2),
        IndexedDiff.Change.Move(4, 3),
        IndexedDiff.Change.Add(4, "2"),
        IndexedDiff.Change.Add(5, "b"),
      )
  }
}