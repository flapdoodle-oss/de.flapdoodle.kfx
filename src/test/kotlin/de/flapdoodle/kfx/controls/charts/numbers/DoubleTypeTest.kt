package de.flapdoodle.kfx.controls.charts.numbers

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
  }

  @Test
  fun unit() {
    val testee = DoubleType.Unit(0.1)

    assertThat(testee.unitsBetween(0.0, 9.9999)).isEqualTo(99)

    assertThat(testee.firstAfter(0.0002)).isCloseTo(0.1, maxDelta)
    assertThat(testee.firstAfter(0.1002)).isCloseTo(0.2, maxDelta)
    assertThat(testee.firstAfter(10.1002)).isCloseTo(10.2, maxDelta)

    assertThat(testee.next(10.0, 3)).isCloseTo(10.3, maxDelta)
  }
}