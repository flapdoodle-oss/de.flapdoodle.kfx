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