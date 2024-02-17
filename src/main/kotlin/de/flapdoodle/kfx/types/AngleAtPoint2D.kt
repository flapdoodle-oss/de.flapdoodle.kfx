package de.flapdoodle.kfx.types

import javafx.geometry.Point2D

data class AngleAtPoint2D(override val point2D: Point2D, override val angle: Double): AngleAndPoint2D {
    constructor(x: Double, y: Double, angle: Double): this(Point2D(x,y), angle)
}