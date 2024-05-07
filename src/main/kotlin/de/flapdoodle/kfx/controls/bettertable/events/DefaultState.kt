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
import javafx.beans.property.ReadOnlyObjectProperty

class DefaultState<T : Any>(
  private val context: EventContext<T>
) : StateWithContext<T>(context) {
  override fun onEvent(event: TableEvent.RequestEvent<T>): State.NextState<T> {
    when (event) {
      is TableEvent.RequestFocus<T, out Any> -> {
        return State.NextState(FocusState(this, context), event)
      }

      is TableEvent.HasFocus<T, out Any> -> {
        return State.NextState(FocusState(this, context), event)
      }

      is TableEvent.MayInsertRow<T> -> {
        return State.NextState(DelayedState(this) {
          // TODO hmm..
          ShowInsertRowState(this, context).onEvent(event).state
        })
      }

      is TableEvent.RequestInsertRow<T> -> {
        val index = context.rows.value.indexOf(event.row)
        val insertIndex = index + if (event.position == TableEvent.InsertPosition.BELOW) 1 else 0
        return State.NextState(InsertRowState(this,context, event.row, context.changeListener.emptyRow(insertIndex), insertIndex),event)
      }

      is TableEvent.EmptyRows<T> -> {
        return State.NextState(InsertRowState(this,context, null, context.changeListener.emptyRow(0), 0),event)
      }

      is TableEvent.HasRows<T> -> {
        onTableEvent(TableEvent.HideInsertFirstRow())
      }

      is TableEvent.DeleteRow<T> -> {
        removeRow(event.row)
      }
      is TableEvent.MouseExitRows<T> -> {
        // do nothing
      }
      is TableEvent.RequestResizeColumn<T, out Any> -> {
        onTableEvent(event.ok())
      }

      is TableEvent.LostFocus<T, out Any> -> {
        // just ignore
      }

      else -> {
        throw IllegalArgumentException("not implemented: $event")
      }
    }
    return State.NextState(this)
  }


  companion object {
    fun <T : Any> eventListener(
      rows: ReadOnlyObjectProperty<List<T>>,
      columns: ReadOnlyObjectProperty<List<Column<T, out Any>>>,
      changeListener: TableChangeListener<T>,
      onTableEvent: (TableEvent.ResponseEvent<T>) -> Unit
    ): StateEventListener<T> {
      return StateEventListener(
        DefaultState(
          EventContext(rows, columns, changeListener, onTableEvent)
        )
      )
    }
  }
}