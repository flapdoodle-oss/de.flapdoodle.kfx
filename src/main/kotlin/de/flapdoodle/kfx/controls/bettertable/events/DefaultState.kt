package de.flapdoodle.kfx.controls.bettertable.events

import de.flapdoodle.kfx.controls.bettertable.CellChangeListener
import de.flapdoodle.kfx.controls.bettertable.Column
import javafx.beans.property.ReadOnlyObjectProperty

class DefaultState<T : Any>(
  private val context: EventContext<T>
) : State<T> {
  override fun onEvent(event: TableEvent.RequestEvent<T>): State<T> {
    when (event) {
      is TableEvent.RequestFocus<T, out Any> -> {
        return FocusState(this, context).onEvent(event)
      }

      is TableEvent.RequestInsertRow<T> -> {
        return DelayedState(this) {
          InsertRowState(this, context).onEvent(event)
        }
      }
      is TableEvent.MouseExitRows<T> -> {
        // do nothing
      }
      is TableEvent.ResizeColumn<T, out Any> -> {
        println("TODO: not implemented right now: $event")
      }

      else -> {
        throw IllegalArgumentException("not implemented: $event")
      }
    }
    return this
  }


  companion object {
    fun <T : Any> eventListener(
      rows: ReadOnlyObjectProperty<List<T>>,
      columns: ReadOnlyObjectProperty<List<Column<T, out Any>>>,
      changeListener: CellChangeListener<T>,
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