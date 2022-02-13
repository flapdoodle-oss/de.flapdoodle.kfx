package de.flapdoodle.kfx.types

import de.flapdoodle.kfx.extensions.layoutPosition
import de.flapdoodle.kfx.extensions.plus
import de.flapdoodle.kfx.types.Direction.*
import javafx.geometry.Point2D
import javafx.scene.layout.Region

val Region.rawLayoutBounds: LayoutBounds
    get() = LayoutBounds(layoutPosition, width, height)

data class LayoutBounds(val layoutPosition: Point2D, val width: Double, val height: Double) {

    fun expand(direction: Direction, diff: Double): LayoutBounds {
        return when (direction) {
            RIGHT -> copy(width = width + diff)
            LEFT -> copy(layoutPosition = layoutPosition + Point2D(diff,0.0), width = width - diff)
            BOTTOM -> copy(height = height + diff)
            TOP -> copy(layoutPosition = layoutPosition + Point2D(0.0, diff), height = height - diff)
        }
    }
}