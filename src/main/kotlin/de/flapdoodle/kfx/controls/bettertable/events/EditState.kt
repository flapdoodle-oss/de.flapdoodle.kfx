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

class EditState<T : Any>(
  private val defaultState: State<T>,
  private val context: EventContext<T>
) : StateWithContext<T>(context) {
  private var lastEdit: TableEvent.RequestEdit<T, out Any>? = null

  override fun onEvent(event: TableEvent.RequestEvent<T>): State<T> {
    when (event) {
      is TableEvent.RequestEdit<T, out Any> -> {
        lastEdit?.let {
          onTableEvent(TableEvent.StopEdit(it.row, it.column))
        }
        lastEdit = event
        onTableEvent(TableEvent.StartEdit(event.row, event.column))
      }
      is TableEvent.CommitChange<T, out Any> -> {
        onTableEvent(event.stopEvent())
        val changed = changeCellAndUpdateRow(event.row, event.asCellChange())
        return FocusState(defaultState, context).onEvent(TableEvent.RequestFocus(changed, event.column))
      }
      is TableEvent.AbortChange<T, out Any> -> {
        onTableEvent(event.ok())
        return FocusState(defaultState, context).onEvent(TableEvent.RequestFocus(event.row, event.column))
      }
      is TableEvent.LostFocus<T, out Any> -> {
        onTableEvent(event.ok())
        return FocusState(defaultState, context).onEvent(TableEvent.RequestFocus(event.row, event.column))
      }
      else -> {
//        println("EDIT MODE - Ignore: $event")
      }
    }
    return this
  }
}