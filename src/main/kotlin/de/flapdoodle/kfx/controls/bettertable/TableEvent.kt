package de.flapdoodle.kfx.controls.bettertable

sealed class TableEvent<T: Any> {

  sealed class RequestEvent<T: Any>() : TableEvent<T>()
  class MouseExitRows<T: Any>(): RequestEvent<T>()

  sealed class RowTriggered<T: Any>(open val row: T): RequestEvent<T>()
  data class RequestInsertRow<T: Any>(override val row: T, val position: InsertPosition): RowTriggered<T>(row) {
    fun ok(): ResponseEvent<T> = ShowInsertRow(row, position)
    fun undo(): ResponseEvent<T> = HideInsertRow(row)
  }

  sealed class CellTriggered<T: Any, C: Any>(open val row: T, open val column: Column<T, C>): RequestEvent<T>()
  data class RequestFocus<T: Any, C: Any>(override val row: T, override val column: Column<T, C>): CellTriggered<T, C>(row, column)
  data class RequestEdit<T: Any, C: Any>(override val row: T, override val column: Column<T, C> ): CellTriggered<T, C>(row, column)
  data class CommitChange<T: Any, C: Any>(override val row: T, override val column: Column<T, C>, val value: C?): CellTriggered<T, C>(row, column) {
    fun asCellChange(): CellChangeListener.Change<T, C> {
      return CellChangeListener.Change(column, value)
    }

    fun stopEvent() = StopEdit(row,column)
  }

  sealed class ResponseEvent<T: Any>() : TableEvent<T>()
  sealed class ToRow<T: Any>(open val row: T): ResponseEvent<T>()
  data class ShowInsertRow<T: Any>(override val row: T, val position: InsertPosition): ToRow<T>(row)
  data class HideInsertRow<T: Any>(override val row: T): ToRow<T>(row)

  sealed class ToCell<T: Any, C: Any>(open val row: T, open val column: Column<T, C>): ResponseEvent<T>()
  data class Focus<T: Any, C: Any>(override val row: T, override val column: Column<T, C>): ToCell<T, C>(row, column)
  data class StartEdit<T: Any, C: Any>(override val row: T, override val column: Column<T, C> ): ToCell<T, C>(row, column)
  class StopEdit<T: Any, C: Any>(override val row: T, override val column: Column<T, C>): ToCell<T, C>(row, column)

  class NextCell<T: Any, C: Any>(override val row: T, override val column: Column<T, C>, val direction: Direction): CellTriggered<T, C>(row, column) {
    fun asFocusEvent(rows: List<T>, columns: List<Column<T, out Any>>): Focus<T, out Any>? {
      val rowIndex = rows.indexOf(row)
      val columnIndex = columns.indexOf(column)
      if (rowIndex!=-1 && columnIndex!=-1) {
        if (direction == Direction.NEXT) {
          var newColumnIndex = columnIndex + 1
          var newRowIndex = rowIndex
          if (newColumnIndex >= columns.size) {
            newColumnIndex = 0
            newRowIndex + 1
            if (newRowIndex >= rows.size) {
              newRowIndex = rows.size - 1
            }
          }
          return Focus(rows[newRowIndex], columns[newColumnIndex])
        } else {
          val newRowIndex = when (direction) {
            Direction.UP -> (rowIndex - 1).coerceAtLeast(0)
            Direction.DOWN -> (rowIndex + 1).coerceAtMost(rows.size - 1)
            else -> rowIndex
          }

          val newColumnIndex = when (direction) {
            Direction.LEFT -> (columnIndex - 1).coerceAtLeast(0)
            Direction.RIGHT -> (columnIndex + 1).coerceAtMost(columns.size - 1)
            else -> columnIndex
          }

          return Focus(rows[newRowIndex], columns[newColumnIndex])
        }
      }
      return null
    }
  }

  enum class InsertPosition {
    ABOVE, BELOW
  }

  enum class Direction {
    LEFT, RIGHT, UP, DOWN, NEXT
  }
}