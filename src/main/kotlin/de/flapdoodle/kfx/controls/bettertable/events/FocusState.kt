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

class FocusState<T : Any>(
  private val defaultState: State<T>,
  private val context: EventContext<T>
) : StateWithContext<T>(context) {
  private var lastFocusEvent: TableEvent.Focus<T, out Any>? = null

  override fun onEvent(event: TableEvent.RequestEvent<T>): State<T> {
    when (event) {
      is TableEvent.HasFocus<T, out Any> -> {
        lastFocusEvent = event.fakedOk()
      }
      is TableEvent.RequestFocus<T, out Any> -> {
        lastFocusEvent = event.ok()
        onTableEvent(event.ok())
      }
      is TableEvent.NextCell<T, out Any> -> {
        val nextEvent = event.asFocusEvent(context.rows.value, context.columns.value)
        if (nextEvent != null) {
          lastFocusEvent = nextEvent
          onTableEvent(nextEvent)
        }
      }
      is TableEvent.RequestEdit<T, out Any> -> {
        require(event.column.editable) {"column is not editable: ${event.column} (${event.row})"}
        return EditState(defaultState, context).onEvent(event)
      }

      is TableEvent.MayInsertRow<T> -> {
        if (event.row != lastFocusEvent?.row) {
          return DelayedState(this) {
//            lastFocusEvent?.let {
//              onTableEvent(TableEvent.Blur(it.row, it.column))
//            }
            ShowInsertRowState(defaultState, context).onEvent(event)
          }
        }
      }
      is TableEvent.MouseExitRows<T> -> {
        // ignore
      }
      else -> {
        lastFocusEvent?.let {
//          onTableEvent(TableEvent.Blur(it.row, it.column))
          onTableEvent(TableEvent.FocusTable())
        }
        return defaultState.onEvent(event)
      }
    }
    return this
  }
}