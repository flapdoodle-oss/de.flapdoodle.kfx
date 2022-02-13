package de.flapdoodle.kfx.extensions

import javafx.geometry.Point2D
import javafx.scene.input.MouseEvent

val MouseEvent.screenPosition: Point2D
    get() = Point2D(this.screenX, this.screenY)

val MouseEvent.localPosition: Point2D
    get() = Point2D(this.x, this.y)
