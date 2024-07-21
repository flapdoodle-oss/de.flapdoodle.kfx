package de.flapdoodle.kfx.layout.grid2

import de.flapdoodle.kfx.layout.grid.GridMap

data class PositionMap<T: Any>(
  private val map: Map<T, Pos>
) {
  private val columnSet = map.values.flatMap { it.column..<it.column + it.columnSpan }.toSet().sorted()
  private val rowSet = map.values.flatMap { it.row..<it.row + it.rowSpan }.toSet().sorted()

  fun columns() = columnSet
  fun rows() = rowSet
  fun values() = map.keys

  fun <D : Any> mapColumns(allColumnRows: (Int, Map<T, Pos>) -> D): List<D> {
    return columnSet.map { column ->
      val matchingRows = map.filter { it.value.matchesColumn(column) }
      allColumnRows(column, matchingRows)
    }
  }

  fun <D : Any> mapRows(allRowsColumns: (Int, Map<T, Pos>) -> D): List<D> {
    return rowSet.map { row ->
      val matchingColumns = map.filter { it.value.matchesRow(row) }
      allRowsColumns(row, matchingColumns)
    }
  }

  operator fun get(item: T): Pos? {
    return map[item]
  }
}

