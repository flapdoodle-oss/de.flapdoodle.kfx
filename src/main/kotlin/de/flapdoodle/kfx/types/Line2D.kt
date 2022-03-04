package de.flapdoodle.kfx.types

import javafx.geometry.Point2D
import javafx.scene.transform.Affine

fun Point2D.to(other: Point2D) = Line2D(this, other)

data class Line2D(val start: Point2D, val end: Point2D) {

    fun positionAt(position: Percent, distance: Double, offset: Double): Point2D {

        val diff = end.subtract(start)

        val base = diff.multiply(position.value).add(start)

        val offsetPoint = Point2D(offset, -distance)

        val baseAngle = Point2D(1.0, 0.0).angle(diff)

        val rotation = Affine.rotate(baseAngle + Math.PI/4.0, 0.0, 0.0)

        val result = rotation.transform(offsetPoint).add(base)

        return result
    }
}