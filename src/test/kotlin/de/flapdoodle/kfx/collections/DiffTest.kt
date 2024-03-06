package de.flapdoodle.kfx.collections

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DiffTest {
  @Test
  fun someChange() {
    val old = listOf(1 to "A", 2 to "B", 3 to "C")
    val new = listOf(1 to "A", 2 to "b", 4 to "X")
    val change = Diff.between(old, new, Pair<Int, String>::first)

    assertThat(change.notChanged)
      .containsExactlyInAnyOrder(1 to "A")
    assertThat(change.modified)
      .containsExactlyInAnyOrder((2 to "B") to (2 to "b"))
    assertThat(change.added)
      .containsExactlyInAnyOrder(4 to "X")
    assertThat(change.removed)
      .containsExactlyInAnyOrder(3 to "C")
  }

  @Test
  fun listDiff() {
    val old = listOf("A", "B", "B", "C")
    val new = listOf("A", "b", "A", "X")
    val change = Diff.between(old, new) { it }

    assertThat(change.notChanged)
      .containsExactlyInAnyOrder("A")
    assertThat(change.modified)
      .isEmpty()
    assertThat(change.added)
      .containsExactlyInAnyOrder("b", "X")
    assertThat(change.removed)
      .containsExactlyInAnyOrder("B", "C")
  }
}