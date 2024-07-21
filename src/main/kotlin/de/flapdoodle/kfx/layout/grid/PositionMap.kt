package de.flapdoodle.kfx.layout.grid

data class PositionMap<T: Any>(
  private val map: Map<T, Pos>
) {

  private val columns = map.values.flatMap { listOf(it.column, it.column + it.columnSpan - 1) }.toSet()
  private val rows = map.values.flatMap { listOf(it.row, it.row + it.rowSpan - 1) }.toSet()
  private val columnSet = if (columns.isNotEmpty()) (columns.minOf { it }..columns.maxOf { it }).toSet().sorted() else emptySet<Int>()
  private val rowSet = if (rows.isNotEmpty()) (rows.minOf { it }..rows.maxOf { it }).toSet().sorted() else emptySet<Int>()

//  private val columnSet = map.values.flatMap { it.column..<it.column + it.columnSpan }.toSet().sorted()
//  private val rowSet = map.values.flatMap { it.row..<it.row + it.rowSpan }.toSet().sorted()

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

