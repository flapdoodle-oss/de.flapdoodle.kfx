package de.flapdoodle.kfx.types

import javafx.geometry.Point2D
import javafx.scene.paint.Color

data class ColoredAngleAtPoint2D(override val point2D: Point2D, override val angle: Double, val color: Color): AngleAndPoint2D {
    constructor(x: Double, y: Double, angle: Double, color: Color): this(Point2D(x,y), angle, color)
}