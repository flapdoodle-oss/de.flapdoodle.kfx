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