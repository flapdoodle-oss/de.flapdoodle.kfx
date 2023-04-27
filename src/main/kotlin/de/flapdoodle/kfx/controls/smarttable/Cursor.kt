package de.flapdoodle.kfx.controls.smarttable

data class Cursor<T: Any>(
  val column: SmartColumn<T, out Any>,
  val row: Int
) {
}