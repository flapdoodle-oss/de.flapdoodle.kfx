package de.flapdoodle.kfx.layout.decoration

import de.flapdoodle.kfx.types.UnitInterval

data class Position(
    val base: Base,
    val position: UnitInterval,
    val distance: Double,
    val offset: Double = 0.0
) {
    
}