package de.flapdoodle.kfx.controls.charts

fun interface RangeFactory<T: Any> {
    fun rangeOf(values: List<T>) : Range<T>
}