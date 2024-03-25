package de.flapdoodle.kfx.controls.bettertable.events

class InsertRowState<T : Any>(
  private val defaultState: State<T>,
  private val context: EventContext<T>,
  private val row: T,
  private val insertIndex: Int
) : StateWithContext<T>(context) {
  private var currentRow: T = row
  private var lastFocusEvent: TableEvent.Focus<T, out Any>? = null

  override fun onEvent(event: TableEvent.RequestEvent<T>): State<T> {
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
        currentRow = changeCell(event.row, event.asCellChange())
        onTableEvent(TableEvent.UpdateInsertRow(currentRow))
//        onTableEvent(TableEvent.Focus(currentRow, event.column))
//        onTableEvent(event.stopEvent())
//        val changed = changeCell(event.row, event.asCellChange())
//        return FocusState(defaultState, context).onEvent(TableEvent.RequestFocus(changed, event.column))
      }
      is TableEvent.CommitChange<T, out Any> -> {
        currentRow = changeCell(event.row, event.asCellChange())
        onTableEvent(TableEvent.UpdateInsertRow(currentRow))
        onTableEvent(TableEvent.StopInsertRow(currentRow))
        insertRow(insertIndex, currentRow)
        return defaultState
//        onTableEvent(TableEvent.UpdateInsertRow(currentRow))
//        onTableEvent(TableEvent.Focus(currentRow, event.column))
//        onTableEvent(event.stopEvent())
//        val changed = changeCell(event.row, event.asCellChange())
//        return FocusState(defaultState, context).onEvent(TableEvent.RequestFocus(changed, event.column))
      }
      is TableEvent.AbortChange<T, out Any> -> {
        onTableEvent(TableEvent.StopInsertRow(event.row))
        return defaultState
      }
      is TableEvent.RequestFocus<T, out Any> -> {
//        require(event.row == currentRow) { "focus for different row: ${event.row} != $currentRow"}
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
        println("$this: ignore $event")
      }
    }
    return this
  }
}