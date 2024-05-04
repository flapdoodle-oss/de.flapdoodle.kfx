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
    val someDouble = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble())

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

    assertThat(testee.offset(BigDecimal.ZERO, BigDecimal.TEN, 100.0, BigDecimal.valueOf(3L))).isEqualTo(30.0)
  }

  @Test
  fun units() {
    val testee = BigDecimalType

    assertThat(testee.units(BigDecimal.valueOf(0.0), BigDecimal.valueOf(10.0)))
      .containsExactly(
        BigDecimalType.Unit(BigDecimal.valueOf(10)),
        BigDecimalType.Unit(BigDecimal.valueOf(2)),
        BigDecimalType.Unit(BigDecimal.valueOf(1)),
        BigDecimalType.Unit(BigDecimal.valueOf(0.2)),
        BigDecimalType.Unit(BigDecimal.valueOf(0.1))
      )

    assertThat(testee.units(BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.2)))
      .containsExactly(
        BigDecimalType.Unit(BigDecimal.valueOf(0.1)),
        BigDecimalType.Unit(BigDecimal.valueOf(0.02)),
        BigDecimalType.Unit(BigDecimal.valueOf(0.01)),
        BigDecimalType.Unit(BigDecimal.valueOf(0.002)),
        BigDecimalType.Unit(BigDecimal.valueOf(0.001))
      )
  }

  @Test
  fun unit() {
    val testee = BigDecimalType.Unit(BigDecimal.valueOf(0.1))

    assertThat(testee.unitsBetween(BigDecimal.valueOf(0.0), BigDecimal.valueOf(9.9999))).isEqualTo(99)

    assertThat(testee.firstUnit(BigDecimal.valueOf(0.0002))).isCloseTo(BigDecimal.valueOf(0.1), maxDelta)
    assertThat(testee.firstUnit(BigDecimal.valueOf(0.1002))).isCloseTo(BigDecimal.valueOf(0.2), maxDelta)
    assertThat(testee.firstUnit(BigDecimal.valueOf(10.1002))).isCloseTo(BigDecimal.valueOf(10.2), maxDelta)

    assertThat(testee.next(BigDecimal.valueOf(10.0), 3)).isCloseTo(BigDecimal.valueOf(10.3), maxDelta)
  }
}