package de.flapdoodle.kfx.controls.bettertable

sealed class TableEvent<T: Any> {
  data class Focus<T: Any>(val row: T, val column: Column<T, out Any>): TableEvent<T>()
}