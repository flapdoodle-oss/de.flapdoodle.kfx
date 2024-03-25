package de.flapdoodle.kfx.controls.bettertable.events

class InsertRowState<T : Any>(
  private val defaultState: State<T>,
  private val context: EventContext<T>
) : StateWithContext<T>(context) {
  private lateinit var currentRow: T
  private var lastFocusEvent: TableEvent.Focus<T, out Any>? = null

  override fun onEvent(event: TableEvent.RequestEvent<T>): State<T> {
    when (event) {
      is TableEvent.HasRows<T> -> {
        return defaultState.onEvent(event)
      }
      is TableEvent.EmptyRows<T> -> {
        currentRow = context.changeListener.emptyRow(0)
        onTableEvent(TableEvent.InsertFirstRow(currentRow))
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
        currentRow = changeCellAndInsertRow(0, event.row, event.asCellChange())
        return defaultState
//        onTableEvent(TableEvent.UpdateInsertRow(currentRow))
//        onTableEvent(TableEvent.Focus(currentRow, event.column))
//        onTableEvent(event.stopEvent())
//        val changed = changeCell(event.row, event.asCellChange())
//        return FocusState(defaultState, context).onEvent(TableEvent.RequestFocus(changed, event.column))
      }
      is TableEvent.RequestFocus<T, out Any> -> {
        require(event.row == currentRow) { "focus for different row: ${event.row} != $currentRow"}
        lastFocusEvent = event.ok()
        onTableEvent(event.ok())
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

      else -> {
//        println("$this: unknown $event")
//        return defaultState.onEvent(event)
        println("$this: ignore $event")
      }
    }
    return this
  }
}