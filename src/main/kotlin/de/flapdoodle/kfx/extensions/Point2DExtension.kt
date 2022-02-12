package de.flapdoodle.kfx.extensions

import javafx.geometry.Point2D

operator fun Point2D.minus(other: Point2D): Point2D {
    return Point2D(this.x - other.x, this.y - other.y)
}

operator fun Point2D.plus(other: Point2D): Point2D {
    return Point2D(this.x + other.x, this.y + other.y)
}