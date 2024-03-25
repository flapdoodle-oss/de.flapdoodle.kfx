package de.flapdoodle.kfx.controls.bettertable.events

import de.flapdoodle.kfx.controls.bettertable.Column
import de.flapdoodle.kfx.controls.bettertable.TableChangeListener
import javafx.beans.property.ReadOnlyObjectProperty

class DefaultState<T : Any>(
  private val context: EventContext<T>
) : StateWithContext<T>(context) {
  override fun onEvent(event: TableEvent.RequestEvent<T>): State<T> {
    when (event) {
      is TableEvent.RequestFocus<T, out Any> -> {
        return FocusState(this, context).onEvent(event)
      }

      is TableEvent.HasFocus<T, out Any> -> {
        return FocusState(this, context).onEvent(event)
      }

      is TableEvent.RequestInsertRow<T> -> {
        return DelayedState(this) {
          ShowInsertRowState(this, context).onEvent(event)
        }
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

      is TableEvent.EmptyRows<T> -> {
        return InsertRowState(this,context).onEvent(event)
      }

      is TableEvent.HasRows<T> -> {
        onTableEvent(TableEvent.HideInsertFirstRow())
      }

      is TableEvent.LostFocus<T, out Any> -> {
        // just ignore
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