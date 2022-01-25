/**
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
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class GridMapTest {

    @Test
    fun `map columns must give matching entries`() {
        val testee = GridMap(mapOf(
            GridMap.Pos(0, 0) to "(0,0)",
            GridMap.Pos(1, 0) to "(1,0)",
            GridMap.Pos(1, 1) to "(1,1)"
        ))

        val result = testee.mapColumns { _, list -> list.joinToString(separator = "|") }

        assertThat(result)
            .containsExactly("(0,0)","(1,0)|(1,1)")
    }

    @Test
    fun `map rows must give matching entries`() {
        val testee = GridMap(mapOf(
            GridMap.Pos(0, 0) to "(0,0)",
            GridMap.Pos(1, 0) to "(1,0)",
            GridMap.Pos(1, 1) to "(1,1)"
        ))

        val result = testee.mapRows { _,list -> list.joinToString(separator = "|") }

        assertThat(result)
            .containsExactly("(0,0)|(1,0)","(1,1)")
    }

}