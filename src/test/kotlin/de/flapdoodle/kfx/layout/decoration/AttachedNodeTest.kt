package de.flapdoodle.kfx.layout.decoration

import de.flapdoodle.kfx.isNearlyEqualTo
import de.flapdoodle.kfx.types.UnitInterval
import javafx.geometry.BoundingBox
import javafx.geometry.Point2D
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AttachedNodeTest {

    @Test
    fun sample() {
        val sourceBounds = BoundingBox(10.0,5.0, 20.0, 30.0)
        val sourcePosition = Position(Base.RIGHT, UnitInterval.HALF, 10.0, 0.0)
        val attachmentBounds = BoundingBox(0.0, 0.0, 10.0, 10.0)
        val attachmentPosition = Position(Base.LEFT, UnitInterval.HALF, 5.0, 0.0)

        val result = AttachedNode.offset(sourceBounds, sourcePosition, attachmentBounds, attachmentPosition)

        assertThat(result).isNearlyEqualTo(Point2D(45.0,15.0))
    }
}