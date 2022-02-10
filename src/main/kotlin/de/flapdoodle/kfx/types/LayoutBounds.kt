package de.flapdoodle.kfx.types

import de.flapdoodle.kfx.types.Direction.*
import javafx.scene.layout.Region

val Region.rawLayoutBounds: LayoutBounds
    get() = LayoutBounds(layoutX, layoutY, width, height)

data class LayoutBounds(val x: Double, val y: Double, val width: Double, val height: Double) {

    fun expand(direction: Direction, diff: Double): LayoutBounds {
        return when (direction) {
            RIGHT -> copy(width = width + diff)
            LEFT -> copy(x = x + diff, width = width - diff)
            BOTTOM -> copy(height = height + diff)
            TOP -> copy(y = y + diff, height = height - diff)
        }
    }
}