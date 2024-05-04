package de.flapdoodle.kfx.controls.charts.numbers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.concurrent.ThreadLocalRandom

class LongTypeTest {
  @Test
  fun minMax() {
    val someDouble = ThreadLocalRandom.current().nextLong()

    val testee = LongType

    assertThat(testee.min(emptyList())).isNull()
    assertThat(testee.max(emptyList())).isNull()
    assertThat(testee.min(listOf(someDouble))).isEqualTo(someDouble)
    assertThat(testee.max(listOf(someDouble))).isEqualTo(someDouble)
    assertThat(testee.min(listOf(someDouble + 1, someDouble))).isEqualTo(someDouble)
    assertThat(testee.max(listOf(someDouble, someDouble - 1))).isEqualTo(someDouble)
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

//    Assertions.assertThat(testee.firstUnit(0.0002)).isCloseTo(0.1, maxDelta)
//    Assertions.assertThat(testee.firstUnit(0.1002)).isCloseTo(0.2, maxDelta)
//    Assertions.assertThat(testee.firstUnit(10.1002)).isCloseTo(10.2, maxDelta)
//
//    Assertions.assertThat(testee.next(10.0, 3)).isCloseTo(10.3, maxDelta)
  }

  @Test
  fun unitShouldStartWithMinIfItMatchesUnit() {
    val testee = LongType.Unit(1L)
    assertThat(testee.firstUnit(0L)).isEqualTo(0L)
  }
}