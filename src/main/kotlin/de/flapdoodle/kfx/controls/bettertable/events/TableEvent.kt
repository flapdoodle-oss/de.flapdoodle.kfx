/*
 * Copyright (C) 2022
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.kfx.controls.bettertable.events

import de.flapdoodle.kfx.controls.bettertable.Column
import de.flapdoodle.kfx.controls.bettertable.TableChangeListener

sealed class TableEvent<T: Any> {

  sealed class RequestEvent<T: Any>() : TableEvent<T>()
  class MouseExitRows<T: Any>(): RequestEvent<T>()
  class EmptyRows<T: Any>(): RequestEvent<T>()
  class HasRows<T: Any>(): RequestEvent<T>()

  sealed class ColumnTriggered<T: Any, C: Any>(open val column: Column<T, C>): RequestEvent<T>()
  data class RequestResizeColumn<T: Any, C: Any>(override val column: Column<T, C>): ColumnTriggered<T, C>(column) {
    fun ok(): ResponseEvent<T> = ResizeColumn(column)
  }

  sealed class RowTriggered<T: Any>(open val row: T): RequestEvent<T>()
  data class MayInsertRow<T: Any>(override val row: T, val position: InsertPosition): RowTriggered<T>(row) {
    fun ok(): ResponseEvent<T> = ShowInsertRow(row, position)
    fun undo(): ResponseEvent<T> = HideInsertRow(row)
  }
  data class RequestInsertRow<T: Any>(override val row: T, val position: InsertPosition): RowTriggered<T>(row) {
    
  }
  data class DeleteRow<T: Any>(override val row: T): RowTriggered<T>(row)

  sealed class CellTriggered<T: Any, C: Any>(open val row: T, open val column: Column<T, C>): RequestEvent<T>()
  data class HasFocus<T: Any, C: Any>(override val row: T, override val column: Column<T, C>): CellTriggered<T, C>(row, column) {
    fun fakedOk(): Focus<T, C> = Focus(row, column)
  }

  data class RequestFocus<T: Any, C: Any>(override val row: T, override val column: Column<T, C>): CellTriggered<T, C>(row, column) {
    fun ok(): Focus<T, C> = Focus(row, column)
  }
  data class RequestEdit<T: Any, C: Any>(override val row: T, override val column: Column<T, C>): CellTriggered<T, C>(row, column)
  data class CommitChange<T: Any, C: Any>(override val row: T, override val column: Column<T, C>, val value: C?): CellTriggered<T, C>(row, column) {
    fun asCellChange(): TableChangeListener.CellChange<T, C> {
      return TableChangeListener.CellChange(column, value)
    }

    fun stopEvent() = StopEdit(row,column)
  }
  data class UpdateChange<T: Any, C: Any>(override val row: T, override val column: Column<T, C>, val value: C?): CellTriggered<T, C>(row, column) {
    fun asCellChange(): TableChangeListener.CellChange<T, C> {
      return TableChangeListener.CellChange(column, value)
    }
  }
  data class AbortChange<T: Any, C: Any>(override val row: T, override val column: Column<T, C>): CellTriggered<T, C>(row, column) {
    fun ok() = StopEdit(row,column)
  }
  data class LostFocus<T: Any, C: Any>(override val row: T, override val column: Column<T, C>): CellTriggered<T, C>(row, column) {
    fun ok() = StopEdit(row,column)
  }

  sealed class ResponseEvent<T: Any>() : TableEvent<T>()
  class FocusTable<T: Any>(): ResponseEvent<T>()

  sealed class ToColumn<T: Any, C: Any>(open val column: Column<T, C>): ResponseEvent<T>()
  data class ResizeColumn<T: Any, C: Any>(override val column: Column<T, C>): ToColumn<T, C>(column)

  sealed class ToRow<T: Any>(open val row: T): ResponseEvent<T>()
  data class ShowInsertRow<T: Any>(override val row: T, val position: InsertPosition): ToRow<T>(row)
  data class HideInsertRow<T: Any>(override val row: T): ToRow<T>(row)
  data class InsertRow<T: Any>(override val row: T, val position: InsertPosition, val emptyRow: T): ToRow<T>(row)
  data class UpdateInsertRow<T: Any>(override val row: T): ToRow<T>(row)
  data class StopInsertRow<T: Any>(override val row: T): ToRow<T>(row)

  data class InsertFirstRow<T: Any>(val row: T): ResponseEvent<T>()
  class HideInsertFirstRow<T: Any>(): ResponseEvent<T>()

  sealed class ToCell<T: Any, C: Any>(open val row: T, open val column: Column<T, C>): ResponseEvent<T>()
  data class Focus<T: Any, C: Any>(override val row: T, override val column: Column<T, C>): ToCell<T, C>(row, column)
  data class Blur<T: Any, C: Any>(override val row: T, override val column: Column<T, C>): ToCell<T, C>(row, column)
  data class StartEdit<T: Any, C: Any>(override val row: T, override val column: Column<T, C>): ToCell<T, C>(row, column)
  data class StopEdit<T: Any, C: Any>(override val row: T, override val column: Column<T, C>): ToCell<T, C>(row, column)

  data class NextCell<T: Any, C: Any>(override val row: T, override val column: Column<T, C>, val direction: Direction): CellTriggered<T, C>(row, column) {
    fun asFocusEvent(rows: List<T>, columns: List<Column<T, out Any>>): Focus<T, out Any>? {
      val rowIndex = rows.indexOf(row)
      val columnIndex = columns.indexOf(column)
      if (rowIndex!=-1 && columnIndex!=-1) {
        return when (direction) {
          Direction.NEXT -> {
            var newColumnIndex = columnIndex + 1
            var newRowIndex = rowIndex
            if (newColumnIndex >= columns.size) {
              newColumnIndex = 0
              newRowIndex += 1
              if (newRowIndex >= rows.size) {
                newRowIndex = rows.size - 1
                newColumnIndex = columns.size - 1
              }
            }
            Focus(rows[newRowIndex], columns[newColumnIndex])
          }
          Direction.PREV -> {
            var newColumnIndex = columnIndex - 1
            var newRowIndex = rowIndex
            if (newColumnIndex < 0) {
              newColumnIndex = columns.size-1
              newRowIndex -= 1
              if (newRowIndex < 0) {
                newRowIndex = 0
                newColumnIndex = 0
              }
            }
            Focus(rows[newRowIndex], columns[newColumnIndex])
          }

          else -> {
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

            Focus(rows[newRowIndex], columns[newColumnIndex])
          }
        }
//        if (direction == Direction.NEXT) {
//          var newColumnIndex = columnIndex + 1
//          var newRowIndex = rowIndex
//          if (newColumnIndex >= columns.size) {
//            newColumnIndex = 0
//            newRowIndex + 1
//            if (newRowIndex >= rows.size) {
//              newRowIndex = rows.size - 1
//            }
//          }
//          return Focus(rows[newRowIndex], columns[newColumnIndex])
//        } else {
//          val newRowIndex = when (direction) {
//            Direction.UP -> (rowIndex - 1).coerceAtLeast(0)
//            Direction.DOWN -> (rowIndex + 1).coerceAtMost(rows.size - 1)
//            else -> rowIndex
//          }
//
//          val newColumnIndex = when (direction) {
//            Direction.LEFT -> (columnIndex - 1).coerceAtLeast(0)
//            Direction.RIGHT -> (columnIndex + 1).coerceAtMost(columns.size - 1)
//            else -> columnIndex
//          }
//
//          return Focus(rows[newRowIndex], columns[newColumnIndex])
//        }
      }
      return null
    }
  }

  enum class InsertPosition {
    ABOVE, BELOW
  }

  enum class Direction {
    LEFT, RIGHT, UP, DOWN, NEXT, PREV
  }
}