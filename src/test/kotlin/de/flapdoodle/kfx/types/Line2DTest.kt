package de.flapdoodle.kfx.types

import de.flapdoodle.kfx.isEqualTo
import de.flapdoodle.kfx.isNearlyEqualTo
import javafx.geometry.Point2D
import javafx.scene.transform.Affine
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Line2DTest {

    @Test
    fun leftToRight() {
        val sample = Point2D.ZERO.to(Point2D(10.0, 0.0))

        assertThat(sample.positionAt(UnitInterval.ZERO, 0.5, 0.0))
            .isEqualTo(Point2D(0.0, -0.5), 0.01)
        assertThat(sample.positionAt(UnitInterval(0.5), 0.5, 0.0))
            .isEqualTo(Point2D(5.0, -0.5), 0.01)
        assertThat(sample.positionAt(UnitInterval.ONE, 0.5, 0.0))
            .isEqualTo(Point2D(10.0, -0.5), 0.01)
    }

    @Test
    fun leftToRightMustOffsetY() {
        val sample = Point2D.ZERO.to(Point2D(10.0, 0.0))
        val result = sample.positionAt(UnitInterval.ONE, 0.5, 0.0)

        assertThat(result).isEqualTo(Point2D(10.0, -0.5), 0.01)
    }

    @Test
    fun topToBottomMustOffsetRight() {
        val sample = Point2D.ZERO.to(Point2D(0.0, 10.0))
        val result = sample.positionAt(UnitInterval.ONE, 0.5, 0.0)

        assertThat(result).isEqualTo(Point2D(0.5, 10.0), 0.01)
    }
}