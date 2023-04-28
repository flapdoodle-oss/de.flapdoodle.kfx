package de.flapdoodle.kfx.controls.table

fun interface CellChangeListener<T: Any> {
  fun onChange(row: Int, change: Change<T, out Any>)
  data class Change<T: Any, C: Any>(val column: Column<T, C>, val value: C?)
}