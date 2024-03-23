package de.flapdoodle.kfx.controls.bettertable.events

class InsertRowState<T : Any>(
  private val defaultState: State<T>,
  private val context: EventContext<T>
) : StateWithContext<T>(context) {
  private lateinit var currentRow: T

  override fun onEvent(event: TableEvent.RequestEvent<T>): State<T> {
    when (event) {
      is TableEvent.EmptyRows<T> -> {
        currentRow = context.changeListener.emptyRow(0)
        onTableEvent(TableEvent.InsertFirstRow(currentRow))
        onTableEvent(TableEvent.Focus(currentRow, context.columns.value[0]))
      }
      is TableEvent.CommitChange<T, out Any> -> {
        currentRow = changeCell(event.row, event.asCellChange())
        onTableEvent(TableEvent.UpdateInsertRow(currentRow))
        onTableEvent(TableEvent.Focus(currentRow, event.column))
//        onTableEvent(event.stopEvent())
//        val changed = changeCell(event.row, event.asCellChange())
//        return FocusState(defaultState, context).onEvent(TableEvent.RequestFocus(changed, event.column))
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