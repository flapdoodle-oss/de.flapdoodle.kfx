package de.flapdoodle.kfx.controls.table

data class Cursor<T: Any>(
    val column: SmartColumn<T, out Any>,
    val row: Int
) {
}