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
import java.math.BigDecimal
import java.util.concurrent.ThreadLocalRandom

class BigDecimalTypeTest {
  private val maxDelta = Percentage.withPercentage(0.1)

  @Test
  fun minMax() {
    val someDouble = bd(ThreadLocalRandom.current().nextDouble())

    val testee = BigDecimalType

    assertThat(testee.min(emptyList())).isNull()
    assertThat(testee.max(emptyList())).isNull()
    assertThat(testee.min(listOf(someDouble))).isEqualTo(someDouble)
    assertThat(testee.max(listOf(someDouble))).isEqualTo(someDouble)
    assertThat(testee.min(listOf(someDouble + BigDecimal.ONE, someDouble))).isEqualTo(someDouble)
    assertThat(testee.max(listOf(someDouble, someDouble - BigDecimal.ONE))).isEqualTo(someDouble)
  }

  @Test
  fun offset() {
    val testee = BigDecimalType

    assertThat(testee.offset(BigDecimal.ZERO, BigDecimal.TEN, 100.0, bd(3.0))).isEqualTo(30.0)
  }

  @Test
  fun units() {
    val testee = BigDecimalType

    assertThat(testee.units(bd(0.0), bd(0.0)))
      .isEmpty()

    assertThat(testee.units(bd(0.0), bd(10.0)))
      .containsExactly(
        BigDecimalType.Unit(bd(10)),
        BigDecimalType.Unit(bd(2)),
        BigDecimalType.Unit(bd(1)),
        BigDecimalType.Unit(bd(0.2)),
        BigDecimalType.Unit(bd(0.1))
      )

    assertThat(testee.units(bd(0.1), bd(0.2)))
      .containsExactly(
        BigDecimalType.Unit(bd(0.1)),
        BigDecimalType.Unit(bd(0.02)),
        BigDecimalType.Unit(bd(0.01)),
        BigDecimalType.Unit(bd(0.002)),
        BigDecimalType.Unit(bd(0.001))
      )
  }

  @Test
  fun unit() {
    val testee = BigDecimalType.Unit(bd(0.1))

    assertThat(testee.unitsBetween(bd(0.0), bd(9.9999))).isEqualTo(100)

    assertThat(testee.firstUnit(bd(0.0002))).isCloseTo(bd(0.1), maxDelta)
    assertThat(testee.firstUnit(bd(0.1002))).isCloseTo(bd(0.2), maxDelta)
    assertThat(testee.firstUnit(bd(10.1002))).isCloseTo(bd(10.2), maxDelta)

    assertThat(testee.next(bd(10.0), 3)).isCloseTo(bd(10.3), maxDelta)
  }

  @Test
  fun withUnitOneFirstUnitAndUnitsBetween() {
    val testee = BigDecimalType.Unit(bd(1))

    assertThat(testee.firstUnit(bd(0.0))).isEqualTo(bd(0.0))
    assertThat(testee.firstUnit(bd(0.0001))).isCloseTo(bd(1.0), maxDelta)
    assertThat(testee.firstUnit(bd(0.9))).isCloseTo(bd(1), maxDelta)
    assertThat(testee.firstUnit(bd(1.0000001))).isCloseTo(bd(2.0), maxDelta)
    assertThat(testee.firstUnit(bd(-0.9))).isCloseTo(bd(0.0), maxDelta)
    assertThat(testee.firstUnit(bd(-1.01))).isCloseTo(bd(-1.0), maxDelta)

    assertThat(testee.unitsBetween(bd(-0.00001), bd(1.00001))).isEqualTo(2)
    assertThat(testee.unitsBetween(bd(0.02), bd(1.01))).isEqualTo(1)
    assertThat(testee.unitsBetween(bd(0.011), bd(0.99))).isEqualTo(0)
  }

  @Test
  fun withUnit100FirstUnitAndUnitsBetween() {
    val testee = BigDecimalType.Unit(bd(10.0))

    assertThat(testee.firstUnit(bd(0.0))).isEqualTo(bd(0.0))
    assertThat(testee.firstUnit(bd(0.0001))).isCloseTo(bd(10.0), maxDelta)
    assertThat(testee.firstUnit(bd(9.9))).isCloseTo(bd(10.0), maxDelta)
    assertThat(testee.firstUnit(bd(10.0000001))).isCloseTo(bd(20.0), maxDelta)
    assertThat(testee.firstUnit(bd(-9.9))).isCloseTo(bd(0.0), maxDelta)
    assertThat(testee.firstUnit(bd(-10.01))).isCloseTo(bd(-10.0), maxDelta)

    assertThat(testee.unitsBetween(bd(-0.00001), bd(10.00001))).isEqualTo(2)
    assertThat(testee.unitsBetween(bd(0.02), bd(10.01))).isEqualTo(1)
    assertThat(testee.unitsBetween(bd(0.011), bd(9.99))).isEqualTo(0)
  }


  @Test
  fun unitShouldStartWithMinIfItMatchesUnit() {
    val testee = IntType.Unit(1)
    assertThat(testee.firstUnit(0)).isEqualTo(0L)
  }


  @Test
  fun bug() {
    val testee = BigDecimalType.units(BigDecimal.ONE, BigDecimal.ONE)

    assertThat(testee).isEmpty()
  }

  private fun bd(value: Double): BigDecimal {
    return BigDecimal.valueOf(value)
  }

  private fun bd(value: Long): BigDecimal {
    return BigDecimal.valueOf(value)
  }
}