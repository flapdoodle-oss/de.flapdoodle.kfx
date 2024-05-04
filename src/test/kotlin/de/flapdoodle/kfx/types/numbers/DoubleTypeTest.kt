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

import de.flapdoodle.kfx.types.numbers.DoubleType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Percentage
import org.junit.jupiter.api.Test
import java.util.concurrent.ThreadLocalRandom

class DoubleTypeTest {
  private val maxDelta = Percentage.withPercentage(0.1)

  @Test
  fun minMax() {
    val someDouble = ThreadLocalRandom.current().nextDouble()

    val testee = DoubleType

    assertThat(testee.min(emptyList())).isNull()
    assertThat(testee.max(emptyList())).isNull()
    assertThat(testee.min(listOf(someDouble))).isEqualTo(someDouble)
    assertThat(testee.max(listOf(someDouble))).isEqualTo(someDouble)
    assertThat(testee.min(listOf(someDouble + 1, someDouble))).isEqualTo(someDouble)
    assertThat(testee.max(listOf(someDouble, someDouble - 1))).isEqualTo(someDouble)
  }

  @Test
  fun offset() {
    val testee = DoubleType

    assertThat(testee.offset(0.0, 10.0, 100.0, 3.0)).isEqualTo(30.0)
  }

  @Test
  fun units() {
    val testee = DoubleType

    assertThat(testee.units(0.0, 10.0))
      .containsExactly(
        DoubleType.Unit(10.0),
        DoubleType.Unit(2.0),
        DoubleType.Unit(1.0),
        DoubleType.Unit(0.2),
        DoubleType.Unit(0.1)
      )

    assertThat(testee.units(0.1, 0.2))
      .containsExactly(
        DoubleType.Unit(0.1),
        DoubleType.Unit(0.02),
        DoubleType.Unit(0.01),
        DoubleType.Unit(0.002),
        DoubleType.Unit(0.001)
      )
  }

  @Test
  fun unit() {
    val testee = DoubleType.Unit(0.1)

    assertThat(testee.unitsBetween(0.0, 9.9999)).isEqualTo(99)

    assertThat(testee.firstUnit(0.0002)).isCloseTo(0.1, maxDelta)
    assertThat(testee.firstUnit(0.1002)).isCloseTo(0.2, maxDelta)
    assertThat(testee.firstUnit(10.1002)).isCloseTo(10.2, maxDelta)

    assertThat(testee.next(10.0, 3)).isCloseTo(10.3, maxDelta)
  }

  @Test
  fun unitShouldStartWithMinIfItMatchesUnit() {
    val testee = DoubleType.Unit(1.0)
    assertThat(testee.firstUnit(0.0)).isEqualTo(0.0)
  }
}