package de.flapdoodle.kfx.controls.charts

fun interface Range<T> {
    fun offset(value: T, scale: Double): Double
}
