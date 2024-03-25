package de.flapdoodle.kfx.controls.bettertable

interface TableChangeListener<T: Any> {
  fun changeCell(row: T, change: CellChange<T, out Any>): T
  fun updateRow(row: T, changed: T)
  fun removeRow(row: T)
  fun insertRow(index: Int, row: T): Boolean
  fun emptyRow(index: Int): T

  data class CellChange<T: Any, C: Any>(val column: Column<T, C>, val value: C?)
}