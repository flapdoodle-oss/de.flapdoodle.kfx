package de.flapdoodle.kfx.controls.bettertable

sealed class TableEvent<T: Any> {
  data class Focus<T: Any>(val row: T, val cell: Cell<T, out Any>): TableEvent<T>()
  data class StartEdit<T: Any>(val row: T, val cell: Cell<T, out Any>): TableEvent<T>()
  data class CommitChange<T: Any, C: Any>(val row: T, val column: Column<T, C>, val value: C?) : TableEvent<T>() {
    fun asCellChange(): CellChangeListener.Change<T, C> {
      return CellChangeListener.Change(column, value)
    }

    fun stopEvent() = StopEdit(row,column)
  }

  class StopEdit<T: Any, C: Any>(val row: T, val column: Column<T, C>) : TableEvent<T>()
}