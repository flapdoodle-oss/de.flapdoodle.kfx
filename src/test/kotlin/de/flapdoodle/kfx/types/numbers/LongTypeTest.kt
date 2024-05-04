package de.flapdoodle.kfx.types.numbers

import de.flapdoodle.kfx.types.numbers.LongType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.concurrent.ThreadLocalRandom

class LongTypeTest {
  @Test
  fun minMax() {
    val someValue = ThreadLocalRandom.current().nextLong()

    val testee = LongType

    assertThat(testee.min(emptyList())).isNull()
    assertThat(testee.max(emptyList())).isNull()
    assertThat(testee.min(listOf(someValue))).isEqualTo(someValue)
    assertThat(testee.max(listOf(someValue))).isEqualTo(someValue)
    assertThat(testee.min(listOf(someValue + 1, someValue))).isEqualTo(someValue)
    assertThat(testee.max(listOf(someValue, someValue - 1))).isEqualTo(someValue)
  }

  @Test
  fun offset() {
    val testee = LongType

    assertThat(testee.offset(0L, 10L, 100.0, 3L)).isEqualTo(30.0)
  }

  @Test
  fun units() {
    val testee = LongType

    assertThat(testee.units(0L, 0L))
      .isEmpty()

    assertThat(testee.units(0L, 10L))
      .containsExactly(
        LongType.Unit(10L),
        LongType.Unit(2L),
        LongType.Unit(1L)
      )

    assertThat(testee.units(100L, 1000L))
      .containsExactly(
        LongType.Unit(1000L),
        LongType.Unit(200L),
        LongType.Unit(100L),
        LongType.Unit(20L),
        LongType.Unit(10L)
      )
  }

  @Test
  fun unit() {
    val testee = LongType.Unit(1L)

    assertThat(testee.unitsBetween(0L, 9L)).isEqualTo(9)
    assertThat(testee.firstUnit(1)).isEqualTo(1)
    assertThat(testee.firstUnit(10)).isEqualTo(10)
  }

  @Test
  fun unitShouldStartWithMinIfItMatchesUnit() {
    val testee = LongType.Unit(1L)
    assertThat(testee.firstUnit(0L)).isEqualTo(0L)
  }
}