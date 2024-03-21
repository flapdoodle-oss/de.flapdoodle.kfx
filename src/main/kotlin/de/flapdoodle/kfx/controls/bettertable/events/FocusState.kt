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

      is TableEvent.RequestInsertRow<T> -> {
        if (event.row != lastFocusEvent?.row) {
          return DelayedState(this) {
            lastFocusEvent?.let {
              onTableEvent(TableEvent.Blur(it.row, it.column))
            }
            InsertRowState(defaultState, context).onEvent(event)
          }
        }
      }
      is TableEvent.MouseExitRows<T> -> {
        // ignore
      }
      else -> {
        lastFocusEvent?.let {
          onTableEvent(TableEvent.Blur(it.row, it.column))
        }
        return defaultState.onEvent(event)
      }
    }
    return this
  }
}