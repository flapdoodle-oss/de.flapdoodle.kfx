package de.flapdoodle.kfx.graph.nodes

import de.flapdoodle.kfx.types.Direction
import de.flapdoodle.kfx.types.LayoutBounds
import javafx.scene.Cursor

enum class SizeMode(private val cursor: Cursor) {
    NORTH(Cursor.N_RESIZE),
    NORTHEAST(Cursor.NE_RESIZE),
    EAST(Cursor.E_RESIZE),
    SOUTHEAST(Cursor.SE_RESIZE),
    SOUTH(Cursor.S_RESIZE),
    SOUTHWEST(Cursor.SW_RESIZE),
    WEST(Cursor.W_RESIZE),
    NORTHWEST(Cursor.NW_RESIZE),
    INSIDE(Cursor.MOVE);

    fun cursor() = cursor

    companion object {
        fun guess(
            x: Double,
            y: Double,
            width: Double,
            height: Double,
            DEFAULT_RESIZE_BORDER_TOLERANCE: Double = 8.0
        ): SizeMode? {
            if (x < 0 || y < 0 || x > width || y > height) {
                return null
            }
            val isNorth = y < DEFAULT_RESIZE_BORDER_TOLERANCE
            val isSouth = y > height - DEFAULT_RESIZE_BORDER_TOLERANCE
            val isEast = x > width - DEFAULT_RESIZE_BORDER_TOLERANCE
            val isWest = x < DEFAULT_RESIZE_BORDER_TOLERANCE

            return if (isNorth && isEast) {
                NORTHEAST
            } else if (isNorth && isWest) {
                NORTHWEST
            } else if (isSouth && isEast) {
                SOUTHEAST
            } else if (isSouth && isWest) {
                SOUTHWEST
            } else if (isNorth) {
                NORTH
            } else if (isSouth) {
                SOUTH
            } else if (isEast) {
                EAST
            } else if (isWest) {
                WEST
            } else {
                INSIDE
            }
        }

        fun resize(sizeMode: SizeMode, base: LayoutBounds, diffX: Double, diffY: Double): LayoutBounds {
            return when(sizeMode) {
                NORTH -> base.expand(Direction.TOP, diffY)
                NORTHEAST -> base.expand(Direction.TOP, diffY).expand(Direction.RIGHT, diffX)
                EAST -> base.expand(Direction.RIGHT, diffX)
                SOUTHEAST -> base.expand(Direction.RIGHT, diffX).expand(Direction.BOTTOM, diffY)
                SOUTH -> base.expand(Direction.BOTTOM, diffY)
                SOUTHWEST -> base.expand(Direction.BOTTOM, diffY).expand(Direction.LEFT, diffX)
                WEST -> base.expand(Direction.LEFT, diffX)
                NORTHWEST -> base.expand(Direction.LEFT, diffX).expand(Direction.TOP, diffY)
                else -> base
            }
        }
    }
}