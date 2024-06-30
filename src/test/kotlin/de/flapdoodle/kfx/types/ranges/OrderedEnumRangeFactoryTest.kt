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