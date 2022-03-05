package de.flapdoodle.kfx.types

import javafx.geometry.Point2D
import javafx.scene.transform.Affine

fun Point2D.to(other: Point2D) = Line2D(this, other)

data class Line2D(val start: Point2D, val end: Point2D) {

    fun positionAt(position: UnitInterval, distance: Double, offset: Double): Point2D {

        val diff = end.subtract(start)

        val base = diff.multiply(position.value).add(start)

        val offsetPoint = Point2D(offset, -distance)

        var baseAngle = Point2D(1.0, 0.0).angle(diff)

        if (diff.y < 0) {
            baseAngle = 360 - baseAngle
        }

        val rotation = Affine.rotate(baseAngle, 0.0, 0.0)

        val rotated = rotation.transform(offsetPoint)

        val result = rotated.add(base)

        return result
    }
}