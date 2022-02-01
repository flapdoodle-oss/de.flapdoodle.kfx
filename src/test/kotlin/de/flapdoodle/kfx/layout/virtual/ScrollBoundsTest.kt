package de.flapdoodle.kfx.layout.virtual

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ScrollBoundsTest {

    @Test
    fun itemIsSmallerThanWindowExceedingLowerBound() {
        val result = ScrollBounds.of(100.0, 50.0, -25.0, 0.0)

        assertThat(result)
            .isEqualTo(ScrollBounds(0.0, 75.0, 25.0))
    }

    @Test
    fun itemIsSmallerThanWindowAllVisible() {
        val result = ScrollBounds.of(100.0, 50.0, -25.0, 25.0)

        assertThat(result)
            .isEqualTo(ScrollBounds(25.0, 75.0, 25.0))
    }

    @Test
    fun itemIsSmallerThanWindowExceedingUpperBound() {
        val result = ScrollBounds.of(100.0, 50.0, -25.0, 100.0)

        assertThat(result)
            .isEqualTo(ScrollBounds(25.0, 100.0, 25.0))
    }

    @Test
    fun itemIsSameSizeThanWindowButWithOffset() {
        val result = ScrollBounds.of(100.0, 100.0, -25.0, 0.0)

        assertThat(result)
            .isEqualTo(ScrollBounds(0.0, 25.0, 0.0))
    }

    @Test
    fun itemIsSameSizeThanWindowLowerBoundsMatchs() {
        val result = ScrollBounds.of(100.0, 100.0, -25.0, 25.0)

        assertThat(result)
            .isEqualTo(ScrollBounds(25.0, 25.0, 0.0))
    }

    @Test
    fun itemIsBiggerThanWindow() {
        val result = ScrollBounds.of(100.0, 125.0, -25.0, 0.0)

        assertThat(result)
            .isEqualTo(ScrollBounds(0.0, 25.0, 20.0))
    }

    @Test
    fun itemIsBiggerThanWindowExceedingLowerLimit() {
        val result = ScrollBounds.of(100.0, 125.0, -25.0, -25.0)

        assertThat(result)
            .isEqualTo(ScrollBounds(-25.0, 25.0, 20.0))
    }

    @Test
    fun itemIsBiggerThanWindowExceedingUpperLimit() {
        val result = ScrollBounds.of(100.0, 125.0, -25.0, 50.0)

        assertThat(result)
            .isEqualTo(ScrollBounds(0.0, 50.0, 20.0))
    }
}
