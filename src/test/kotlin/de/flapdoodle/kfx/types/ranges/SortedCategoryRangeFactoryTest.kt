package de.flapdoodle.kfx.types.ranges

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Month

class SortedCategoryRangeFactoryTest {
    @Test
    fun noTicksIfEmpty() {
        val ticks = SortedCategoryRangeFactory<Month>()
            .rangeOf(listOf()).ticks(Month.values().size)

        assertThat(ticks)
            .isEmpty()
    }

    @Test
    fun oneTicksIfOneElement() {
        val ticks = SortedCategoryRangeFactory<Month>()
            .rangeOf(listOf(Month.JUNE)).ticks(Month.values().size)

        assertThat(ticks)
            .hasSize(1)

        assertThat(ticks[0].list)
            .containsExactly(Month.JUNE)
    }

    @Test
    fun allTicksBetweenMinAndMax() {
        val ticks = SortedCategoryRangeFactory<Month>()
            .rangeOf(listOf(Month.FEBRUARY, Month.JULY)).ticks(Month.values().size)

        assertThat(ticks)
            .hasSize(1)

        assertThat(ticks[0].list)
            .containsExactly(Month.FEBRUARY, Month.JULY)
    }

}