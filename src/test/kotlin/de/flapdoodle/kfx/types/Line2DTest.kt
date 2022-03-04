package de.flapdoodle.kfx.types

import javafx.geometry.Point2D
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.ObjectAssert
import org.assertj.core.data.Offset
import org.junit.jupiter.api.Test
import java.util.function.Consumer

internal class Line2DTest {

    @Test
    fun leftToRightMustOffsetY() {
        val sample = Point2D.ZERO.to(Point2D(10.0, 0.0))
        val result = sample.positionAt(Percent(1.0), 0.5, 0.0)

        assertThat(result).isEqualTo(Point2D(10.0, -0.5), 0.01)
    }

    @Test
    fun topToBottomMustOffsetRight() {
        val sample = Point2D.ZERO.to(Point2D(0.0, 10.0))
        val result = sample.positionAt(Percent(1.0), 0.5, 0.0)

        assertThat(result).isEqualTo(Point2D(0.5, 10.0), 0.01)
    }

    fun ObjectAssert<Point2D>.isEqualTo(other: Point2D, delta: Double) {
        satisfies(Consumer {
            assertThat(it.x).describedAs("x").isEqualTo(other.x, Offset.offset(delta))
            assertThat(it.y).describedAs("y").isEqualTo(other.y, Offset.offset(delta))
        })
    }
}