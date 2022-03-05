package de.flapdoodle.kfx

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