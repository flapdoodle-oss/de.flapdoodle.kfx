package de.flapdoodle.kfx.controls.bettertable

fun interface CellChangeListener<T: Any> {
  fun onChange(row: T, change: Change<T, out Any>)
  data class Change<T: Any, C: Any>(val column: Column<T, C>, val value: C?)
}