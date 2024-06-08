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

import de.flapdoodle.kfx.logging.Logging

class InsertRowState<T : Any>(
  private val defaultState: State<T>,
  private val context: EventContext<T>,
  private val reference: T?,
  private val row: T,
  private val insertIndex: Int
) : StateWithContext<T>(context) {
  private val logger = Logging.logger(InsertRowState::class)

  private var currentRow: T = row
  private var lastFocusEvent: TableEvent.Focus<T, out Any>? = null

  override fun onEvent(event: TableEvent.RequestEvent<T>): State.NextState<T> {
    when (event) {
      is TableEvent.HasRows<T> -> {
        return defaultState.onEvent(event)
      }
      is TableEvent.EmptyRows<T> -> {
        onTableEvent(TableEvent.InsertFirstRow(currentRow))
        onTableEvent(TableEvent.Focus(currentRow, context.columns.value[0]))
      }
      is TableEvent.RequestInsertRow<T> -> {
        onTableEvent(TableEvent.InsertRow(event.row, event.position, currentRow))
        onTableEvent(TableEvent.Focus(currentRow, context.columns.value[0]))
      }
      is TableEvent.UpdateChange<T, out Any> -> {
        val changed = changeCell(event.row, event.asCellChange())
        currentRow = changed.row
        val currentColumnErrors = changed.errors.map {
          TableEvent.ColumnError(it.column, it.localizedError)
        }
        onTableEvent(TableEvent.UpdateInsertRow(currentRow, currentColumnErrors))
      }
      is TableEvent.CommitChange<T, out Any> -> {
        val changed = changeCell(event.row, event.asCellChange())
        currentRow = changed.row
        val currentColumnErrors = changed.errors.map {
          TableEvent.ColumnError(it.column, it.localizedError)
        }
        onTableEvent(TableEvent.UpdateInsertRow(currentRow, currentColumnErrors))
        if (changed.hasNoErrors()) {
          onTableEvent(TableEvent.StopInsertRow(currentRow))
          if (insertRow(insertIndex, currentRow)) {
            return defaultState.onEvent(TableEvent.RequestFocus(currentRow, event.column))
          }
        }
      }
      is TableEvent.AbortChange<T, out Any> -> {
        if (reference!=null) {
          onTableEvent(TableEvent.StopInsertRow(event.row))
          return defaultState.onEvent(TableEvent.RequestFocus(reference, event.column))
        }
      }
      is TableEvent.RequestFocus<T, out Any> -> {
        if (event.row == currentRow) {
          lastFocusEvent = event.ok()
          onTableEvent(event.ok())
        } else {
          // no focus on other rows allowed
        }
      }
      is TableEvent.LostFocus<T, out Any> -> {
        // ignore
        lastFocusEvent = null
      }
      is TableEvent.HasFocus<T, out Any> -> {
        lastFocusEvent = event.fakedOk()
      }
      is TableEvent.NextCell<T, out Any> -> {
        val nextEvent = event.asFocusEvent(listOf(currentRow), context.columns.value)
        if (nextEvent!=null) {
          lastFocusEvent = nextEvent
          onTableEvent(nextEvent)
        }
      }

      is TableEvent.RequestResizeColumn<T, out Any> -> {
        onTableEvent(event.ok())
      }

      is TableEvent.MayInsertRow<T> -> {
        // ignore
      }

      else -> {
//        println("$this: unknown $event")
//        return defaultState.onEvent(event)
        logger.info { "$this: ignore $event" }
      }
    }
    return State.NextState(this)
  }
}