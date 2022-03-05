package de.flapdoodle.kfx.layout.decoration

import de.flapdoodle.kfx.types.Direction
import de.flapdoodle.kfx.types.Line2D
import de.flapdoodle.kfx.types.UnitInterval
import de.flapdoodle.kfx.types.to
import javafx.geometry.Bounds
import javafx.geometry.Point2D

data class Position(
    val base: Base,
    val position: UnitInterval,
    val distance: Double,
    val offset: Double = 0.0
) {
    
}