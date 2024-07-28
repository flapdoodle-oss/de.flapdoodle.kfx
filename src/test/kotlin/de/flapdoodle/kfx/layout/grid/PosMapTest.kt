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
package de.flapdoodle.kfx.layout.grid

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PosMapTest {
  @Test
  fun `map columns must give matching entries`() {
    val testee = PositionMap(
      mapOf(
        "(0,0)" to Pos(0, 0),
        "(1,0)" to Pos(1, 0),
        "(2,2)" to Pos(2, 2),
        "(1..2,2)" to Pos(1, 2, 2, 1)
      )
    )

    assertThat(testee.rows())
      .containsExactly(0, 1, 2)

    assertThat(testee.columns())
      .containsExactly(0, 1, 2)

    val mapColumns = testee.mapColumns { _, list -> list.keys.joinToString(separator = "|") }

    assertThat(mapColumns)
      .containsExactly("(0,0)", "(1,0)|(1..2,2)", "(2,2)|(1..2,2)")

    val mapRows = testee.mapRows { _, list -> list.keys.joinToString(separator = "|") }

    assertThat(mapRows)
      .containsExactly("(0,0)|(1,0)", "", "(2,2)|(1..2,2)")
  }

}