package de.flapdoodle.kfx.layout.grid2

data class PositionMap<T: Any>(
  private val map: Map<T, Pos>
) {
  private val columnSet = map.values.flatMap { it.column..<it.column + it.columnSpan }.toSet().sorted()
  private val rowSet = map.values.flatMap { it.row..<it.row + it.rowSpan }.toSet().sorted()

  fun columns() = columnSet
  fun rows() = rowSet

  
}

