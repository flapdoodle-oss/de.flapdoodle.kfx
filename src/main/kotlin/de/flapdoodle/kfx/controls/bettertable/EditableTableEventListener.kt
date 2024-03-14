package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.transitions.DelayAction
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.util.Duration

class EditableTableEventListener<T : Any>(
  internal val rows: ReadOnlyObjectProperty<List<T>>,
  internal val columns: ReadOnlyObjectProperty<List<Column<T, out Any>>>,
  internal val changeListener: CellChangeListener<T>,
  internal val onTableEvent: (TableEvent.ResponseEvent<T>) -> Unit
) : TableRequestEventListener<T> {
  private val delayAction = DelayAction(Duration.millis(700.0))
  private var lastInsertRowRequest: TableEvent.RequestInsertRow<T>? = null

  private enum class Mode {
    DEFAULT, EDIT, REQUEST_INSERT_ROW
  }

  private var currentMode = Mode.DEFAULT

  override fun fireEvent(event: TableEvent.RequestEvent<T>) {
    println("mode: $currentMode, event: $event")

    when (event) {
      is TableEvent.CommitChange<T, out Any> -> {
        currentMode = Mode.DEFAULT
        changeListener.onChange(event.row, event.asCellChange())
        onTableEvent(event.stopEvent())
        onTableEvent(TableEvent.Focus(event.row, event.column))
      }

      is TableEvent.NextCell<T, out Any> -> {
        val nextEvent = event.asFocusEvent(rows.value, columns.value)
        if (nextEvent != null) {
          onTableEvent(nextEvent)
        }
      }

      is TableEvent.RequestEdit<T, out Any> -> {
        currentMode = Mode.EDIT
        onTableEvent(TableEvent.StartEdit(event.row, event.column))
      }

      is TableEvent.RequestFocus<T, out Any> -> {
        onTableEvent(TableEvent.Focus(event.row, event.column))
      }

      is TableEvent.RequestInsertRow<T> -> {
        if (currentMode != Mode.REQUEST_INSERT_ROW) {
          delayAction.call {
            currentMode = Mode.REQUEST_INSERT_ROW
            lastInsertRowRequest = event
            onTableEvent(event.ok())
          }
        } else {
          lastInsertRowRequest?.let {
            onTableEvent(it.undo())
          }
          lastInsertRowRequest = event
          onTableEvent(event.ok())
        }
      }

      is TableEvent.MouseExitRows<T> -> {
        if (currentMode == Mode.REQUEST_INSERT_ROW) {
          lastInsertRowRequest?.let {
            onTableEvent(it.undo())
          }
          lastInsertRowRequest = null
          currentMode = Mode.DEFAULT
        }
      }

      else -> {
        throw IllegalArgumentException("not implemented: $event")
      }
    }
  }
}