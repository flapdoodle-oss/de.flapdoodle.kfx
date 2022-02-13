package de.flapdoodle.kfx.extensions

import javafx.geometry.Point2D

operator fun Point2D.minus(other: Point2D): Point2D {
    return Point2D(this.x - other.x, this.y - other.y)
}

operator fun Point2D.plus(other: Point2D): Point2D {
    return Point2D(this.x + other.x, this.y + other.y)
}

fun Point2D.addX(value: Double): Point2D = Point2D(this.x + value, this.y)
fun Point2D.addY(value: Double): Point2D = Point2D(this.x, this.y + value)
fun Point2D.subX(value: Double): Point2D = addX(-value)
fun Point2D.subY(value: Double): Point2D = addY(-value)
