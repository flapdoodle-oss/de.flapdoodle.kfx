package de.flapdoodle.kfx.types

operator fun Double.times(other: UnitInterval) = other.times(this)

/**
 * https://en.wikipedia.org/wiki/Unit_interval
 */
data class UnitInterval(val value: Double) {
    init {
        require(value>=0.0) {"invalid value: $value < 0.0"}
        require(value <=1.0) {"invalid value: $value > 1.0"}
    }

    operator fun times(other: Double): Double {
        return other*value
    }

    companion object {
        val ZERO=UnitInterval(0.0)
        val HALF=UnitInterval(0.5)
        val ONE=UnitInterval(1.0)
    }
}
