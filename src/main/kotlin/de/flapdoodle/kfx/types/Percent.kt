package de.flapdoodle.kfx.types

operator fun Double.times(other: Percent) = other.times(this)

data class Percent(val value: Double) {
    init {
        require(value>=0.0) {"invalid value: $value < 0.0"}
        require(value <=1.0) {"invalid value: $value > 1.0"}
    }

    operator fun times(other: Double): Double {
        return other*value
    }
}
