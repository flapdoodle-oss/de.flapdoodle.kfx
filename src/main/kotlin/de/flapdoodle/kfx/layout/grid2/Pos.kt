package de.flapdoodle.kfx.layout.grid2

data class Pos(
  val column: Int,
  val row: Int,
  val columnSpan: Int = 1,
  val rowSpan: Int = 1
) {
  private val lastColumn = column + columnSpan
  private val lastRow = row + rowSpan

  init {
    require(column >= 0) { "column must be non-negative but was $column" }
    require(row >= 0) { "row must be non-negative but was $row" }
    require(columnSpan > 0) { "column span must be one ore more but was $columnSpan" }
    require(rowSpan > 0) { "row span must be one or more but was $rowSpan" }
  }

  fun matchesColumn(column: Int): Boolean {
    return this.column <= column && column <= lastColumn
  }

  fun matchesRow(row: Int): Boolean {
    return this.row <= row && row <= lastRow
  }
}
