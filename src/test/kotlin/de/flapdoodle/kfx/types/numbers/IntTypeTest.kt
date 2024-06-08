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
package de.flapdoodle.kfx.types.numbers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.concurrent.ThreadLocalRandom

class IntTypeTest {
    @Test
    fun minMax() {
        val someValue = ThreadLocalRandom.current().nextInt()

        val testee = IntType

        assertThat(testee.min(emptyList())).isNull()
        assertThat(testee.max(emptyList())).isNull()
        assertThat(testee.min(listOf(someValue))).isEqualTo(someValue)
        assertThat(testee.max(listOf(someValue))).isEqualTo(someValue)
        assertThat(testee.min(listOf(someValue + 1, someValue))).isEqualTo(someValue)
        assertThat(testee.max(listOf(someValue, someValue - 1))).isEqualTo(someValue)
    }

    @Test
    fun offset() {
        val testee = IntType

        assertThat(testee.offset(0, 10, 100.0, 3)).isEqualTo(30.0)
    }

    @Test
    fun units() {
        val testee = IntType

        assertThat(testee.units(0, 0))
            .isEmpty()

        assertThat(testee.units(0, 10))
            .containsExactly(
                IntType.Unit(10),
                IntType.Unit(2),
                IntType.Unit(1)
            )

        assertThat(testee.units(100, 1000))
            .containsExactly(
                IntType.Unit(1000),
                IntType.Unit(200),
                IntType.Unit(100),
                IntType.Unit(20),
                IntType.Unit(10)
            )
    }

    @Test
    fun unit() {
        val testee = IntType.Unit(1)

        assertThat(testee.unitsBetween(0, 9)).isEqualTo(9)
        assertThat(testee.firstUnit(1)).isEqualTo(1)
        assertThat(testee.firstUnit(10)).isEqualTo(10)
    }

    @Test
    fun unitShouldStartWithMinIfItMatchesUnit() {
        val testee = IntType.Unit(1)
        assertThat(testee.firstUnit(0)).isEqualTo(0L)
    }
}