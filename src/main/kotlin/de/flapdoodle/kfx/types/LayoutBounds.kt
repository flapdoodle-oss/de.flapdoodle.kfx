package de.flapdoodle.kfx.types

import de.flapdoodle.kfx.extensions.*
import de.flapdoodle.kfx.types.Direction.*
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.scene.layout.Region

val Region.size: Dimension2D
    get() = Dimension2D(width, height)

val Region.rawLayoutBounds: LayoutBounds
    get() = LayoutBounds(layoutPosition, size)

data class LayoutBounds(val layoutPosition: Point2D, val size: Dimension2D) {

    fun expand(direction: Direction, diff: Double): LayoutBounds {
        return when (direction) {
            RIGHT -> copy(size = size.addWidth(diff))
            LEFT -> copy(layoutPosition = layoutPosition.addX(diff), size = size.subWidth(diff))
            BOTTOM -> copy(size = size.addHeight(diff))
            TOP -> copy(layoutPosition = layoutPosition.addY(diff), size = size.subHeight(diff))
        }
    }
}