package de.flapdoodle.kfx.controls.bettertable.events

import de.flapdoodle.kfx.controls.bettertable.CellChangeListener
import de.flapdoodle.kfx.controls.bettertable.Column
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

  data class Context<T : Any>(
    val rows: ReadOnlyObjectProperty<List<T>>,
    val columns: ReadOnlyObjectProperty<List<Column<T, out Any>>>,
    val changeListener: CellChangeListener<T>,
    val onTableEvent: (TableEvent.ResponseEvent<T>) -> Unit
  )

  class EditState<T : Any>(
    private val context: Context<T>
  ) : State<T> {
    override fun onEvent(event: TableEvent.RequestEvent<T>): State<T> {
      when (event) {
        is TableEvent.RequestEdit<T, out Any> -> {
          context.onTableEvent(TableEvent.StartEdit(event.row, event.column))
        }
        is TableEvent.CommitChange<T, out Any> -> {
          context.changeListener.onChange(event.row, event.asCellChange())
          context.onTableEvent(event.stopEvent())
          return FocusState(context).onEvent(TableEvent.RequestFocus(event.row, event.column))
        }
        else -> {
          println("Ignore: $event")
//          throw IllegalArgumentException("not implemented: $event")
        }
      }
      return this
    }
  }

  class FocusState<T : Any>(
    private val context: Context<T>
  ) : State<T> {
    override fun onEvent(event: TableEvent.RequestEvent<T>): State<T> {
      when (event) {
        is TableEvent.RequestFocus<T, out Any> -> {
          context.onTableEvent(event.ok())
        }
        is TableEvent.RequestEdit<T, out Any> -> {
          return EditState(context).onEvent(event)
        }

        else -> {
          println("Ignore: $event")
//          throw IllegalArgumentException("not implemented: $event")
        }
      }
      return this
    }
  }

  class InsertRowState<T : Any>(
    private val context: Context<T>
  ) : State<T> {
    private var lastInsertRowRequest: TableEvent.RequestInsertRow<T>? = null

    override fun onEvent(event: TableEvent.RequestEvent<T>): State<T> {
      when (event) {
        is TableEvent.RequestInsertRow<T> -> {
          lastInsertRowRequest?.let {
            context.onTableEvent(it.undo())
          }
          lastInsertRowRequest = event
          context.onTableEvent(event.ok())
//            delayAction.call {
//              lastInsertRowRequest = event
//              context.onTableEvent(event.ok())
//            }
//          } else {
//            lastInsertRowRequest?.let {
//              onTableEvent(it.undo())
//            }
//            lastInsertRowRequest = event
//            onTableEvent(event.ok())
//          }
        }

        else -> {
//          throw IllegalArgumentException("not implemented: $event")
          lastInsertRowRequest?.let {
            context.onTableEvent(it.undo())
          }
          return DefaultState(context).onEvent(event)
        }
      }
      return this
    }
  }

  class DelayedState<T : Any>(
    private val base: State<T>,
    private val delayedState: State<T>,
    private val delayedEvent: TableEvent.RequestEvent<T>,
  ) : State<T> {
    private val delayAction = DelayAction(Duration.millis(700.0))
    private var current = base

    init {
      delayAction.call {
        current = delayedState.onEvent(delayedEvent)
      }
    }

    override fun onEvent(event: TableEvent.RequestEvent<T>): State<T> {
      delayAction.stop()
      return current.onEvent(event)
    }
  }

  class DefaultState<T : Any>(
    private val context: Context<T>
  ) : State<T> {
    override fun onEvent(event: TableEvent.RequestEvent<T>): State<T> {
      when (event) {
        is TableEvent.RequestFocus<T, out Any> -> {
          return FocusState(context).onEvent(event)
        }

        is TableEvent.RequestInsertRow<T> -> {
          return DelayedState(this, InsertRowState(context), event)
        }
        is TableEvent.MouseExitRows<T> -> {
          // do nothing
        }

        else -> {
          throw IllegalArgumentException("not implemented: $event")
        }
      }
      return this
    }

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
          Context(rows, columns, changeListener, onTableEvent)
        )
      )
    }
  }
}