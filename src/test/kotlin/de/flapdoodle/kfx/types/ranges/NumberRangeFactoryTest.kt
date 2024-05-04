package de.flapdoodle.kfx.types.ranges

import de.flapdoodle.kfx.types.ranges.NumberRangeFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NumberRangeFactoryTest {

  @Test
  fun ticks() {
    val ticks = NumberRangeFactory(Double::class).rangeOf(listOf(0.0, 10.0)).ticks(10)

    assertThat(ticks)
      .hasSize(3)

    assertThat(ticks[0].list)
      .containsExactly(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)
    assertThat(ticks[1].list)
      .containsExactly(0.0, 2.0, 4.0, 6.0, 8.0, 10.0)
    assertThat(ticks[2].list)
      .containsExactly(0.0, 10.0)
  }
}