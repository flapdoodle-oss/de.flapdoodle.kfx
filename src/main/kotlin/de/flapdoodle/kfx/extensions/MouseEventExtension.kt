package de.flapdoodle.kfx.extensions

import javafx.geometry.Point2D
import javafx.scene.input.MouseEvent

val MouseEvent.screen: Point2D
    get() = Point2D(this.screenX, this.screenY)
