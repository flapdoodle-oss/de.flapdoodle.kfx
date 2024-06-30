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
package de.flapdoodle.kfx.types.ranges

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Month

class OrderedEnumRangeFactoryTest {
    @Test
    fun noTicksIfEmpty() {
        val ticks = OrderedEnumRangeFactory(Month::class)
            .rangeOf(listOf()).ticks(Month.values().size)

        assertThat(ticks)
            .isEmpty()
    }

    @Test
    fun oneTicksIfOneElement() {
        val ticks = OrderedEnumRangeFactory(Month::class)
            .rangeOf(listOf(Month.JUNE)).ticks(Month.values().size)

        assertThat(ticks)
            .hasSize(1)

        assertThat(ticks[0].list)
            .containsExactly(Month.JUNE)
    }

    @Test
    fun allTicksBetweenMinAndMax() {
        val ticks = OrderedEnumRangeFactory(Month::class)
            .rangeOf(listOf(Month.FEBRUARY, Month.JULY)).ticks(Month.values().size)

        assertThat(ticks)
            .hasSize(1)

        assertThat(ticks[0].list)
            .containsExactly(Month.FEBRUARY, Month.MARCH, Month.APRIL, Month.MAY, Month.JUNE, Month.JULY)
    }

}