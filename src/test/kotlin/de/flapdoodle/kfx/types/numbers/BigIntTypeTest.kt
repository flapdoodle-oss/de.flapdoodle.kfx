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
import org.assertj.core.data.Percentage
import org.junit.jupiter.api.Test
import java.math.BigInteger
import java.util.concurrent.ThreadLocalRandom

class BigIntTypeTest {
    private val maxDelta = Percentage.withPercentage(0.1)

    @Test
    fun minMax() {
        val someDouble = BigInteger.valueOf(ThreadLocalRandom.current().nextLong())

        val testee = BigIntType

        assertThat(testee.min(emptyList())).isNull()
        assertThat(testee.max(emptyList())).isNull()
        assertThat(testee.min(listOf(someDouble))).isEqualTo(someDouble)
        assertThat(testee.max(listOf(someDouble))).isEqualTo(someDouble)
        assertThat(testee.min(listOf(someDouble + BigInteger.ONE, someDouble))).isEqualTo(someDouble)
        assertThat(testee.max(listOf(someDouble, someDouble - BigInteger.ONE))).isEqualTo(someDouble)
    }

    @Test
    fun offset() {
        val testee = BigIntType

        assertThat(testee.offset(BigInteger.ZERO, BigInteger.TEN, 100.0, BigInteger.valueOf(3L))).isEqualTo(30.0)
    }

    @Test
    fun units() {
        val testee = BigIntType

        assertThat(testee.units(BigInteger.valueOf(0), BigInteger.valueOf(10)))
            .containsExactly(
                BigIntType.Unit(BigInteger.valueOf(10)),
                BigIntType.Unit(BigInteger.valueOf(2)),
                BigIntType.Unit(BigInteger.valueOf(1))
            )

        assertThat(testee.units(BigInteger.valueOf(100), BigInteger.valueOf(1000)))
            .containsExactly(
                BigIntType.Unit(BigInteger.valueOf(1000)),
                BigIntType.Unit(BigInteger.valueOf(200)),
                BigIntType.Unit(BigInteger.valueOf(100)),
                BigIntType.Unit(BigInteger.valueOf(20)),
                BigIntType.Unit(BigInteger.valueOf(10))
            )
    }

    @Test
    fun unit() {
        val testee = BigIntType.Unit(BigInteger.valueOf(1))

        assertThat(testee.unitsBetween(BigInteger.valueOf(0), BigInteger.valueOf(9))).isEqualTo(9)
        assertThat(testee.firstUnit(BigInteger.valueOf(2))).isEqualTo(BigInteger.valueOf(2))
        assertThat(testee.next(BigInteger.valueOf(10), 3)).isEqualTo(BigInteger.valueOf(13))
    }
}