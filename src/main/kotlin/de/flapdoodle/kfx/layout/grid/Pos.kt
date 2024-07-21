package de.flapdoodle.kfx.layout.grid

data class Pos(
    val column: Int,
    val row: Int
) {
    init {
        require(column >= 0) { "invalid column: $column" }
        require(row >= 0) { "invalid row: $row" }
    }
}