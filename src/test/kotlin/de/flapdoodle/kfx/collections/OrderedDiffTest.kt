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

import de.flapdoodle.kfx.collections.OrderedDiff.between
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OrderedDiffTest {

  @Test
  fun orderedChanges() {
    val old = listOf(1 to "A", 2 to "B", 3 to "C", 5 to "D", 6 to "Foo")
    val new = listOf(1 to "A", 2 to "b", 4 to "X", 6 to "Foo", 5 to "D")
    val change = between(old, new, Pair<Int, String>::first)

    assertThat(change.removed)
      .containsExactlyInAnyOrder(3 to "C", 5 to "D")
    assertThat(change.notChanged)
      .containsExactlyInAnyOrder(1 to "A", 6 to "Foo")
    assertThat(change.modified)
      .containsExactlyInAnyOrder((2 to "B") to (2 to "b"))
    assertThat(change.added)
      .containsExactlyInAnyOrder((4 to "X") to null, (5 to "D") to (5 to "D"))
  }
//  @Test
//  fun addToList() {
//    val changes = between(listOf(), listOf("1", "b")) { it }
//
//    assertThat(changes)
//      .containsExactly(
//        OrderedDiff.Change.Add(0, "1"),
//        OrderedDiff.Change.Add(1, "b")
//      )
//  }
//
//  @Test
//  fun removeFromList() {
//    val changes = between(listOf("A", "b"), listOf("b")) { it }
//
//    assertThat(changes)
//      .containsExactly(
//        OrderedDiff.Change.Remove(0)
//      )
//  }
//
//
//  @Test
//  fun removeAndAdd() {
//    val changes = between(listOf("A", "b"), listOf("B", "C", "b")) { it }
//
//    assertThat(changes)
//      .containsExactly(
//        OrderedDiff.Change.Remove(0),
//        OrderedDiff.Change.Add(0, "B"),
//        OrderedDiff.Change.Add(1, "C"),
//        OrderedDiff.Change.Move(0, 2)
//      )
//  }
//
//  @Test
//  fun moveAndModify() {
//    val changes = between(listOf("Aaa", "bee","Cee"), listOf("B", "Ccc", "bee")) { it.first() }
//
//    assertThat(changes)
//      .containsExactly(
//        OrderedDiff.Change.Remove(0),
//        OrderedDiff.Change.Add(0, "B"),
//        OrderedDiff.Change.Modify(1, 1, "Ccc"),
//        OrderedDiff.Change.Move(0, 2),
//      )
//  }
}