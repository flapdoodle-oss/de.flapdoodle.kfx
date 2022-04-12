package de.flapdoodle.kfx

import de.flapdoodle.kfx.types.LayoutBounds
import javafx.geometry.Bounds
import javafx.geometry.Point2D
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.ObjectAssert
import org.assertj.core.data.Offset
import java.util.function.Consumer

fun ObjectAssert<Point2D>.isEqualTo(other: Point2D, delta: Double) {
    extracting(Point2D::getX).satisfies(Consumer {
        assertThat(it).isEqualTo(other.x, Offset.offset(delta))
    })
    extracting(Point2D::getY).satisfies(Consumer {
        assertThat(it).isEqualTo(other.y, Offset.offset(delta))
    })
}

fun ObjectAssert<Point2D>.isNearlyEqualTo(other: Point2D) {
    isEqualTo(other, 0.01)
}

fun ObjectAssert<Bounds>.hasBounds(bounds: LayoutBounds) {
    extracting(Bounds::getMinX).describedAs("minX").isEqualTo(bounds.x)
    extracting(Bounds::getMinY).describedAs("minY").isEqualTo(bounds.y)
    extracting(Bounds::getWidth).describedAs("width").isEqualTo(bounds.width)
    extracting(Bounds::getHeight).describedAs("height").isEqualTo(bounds.height)
}