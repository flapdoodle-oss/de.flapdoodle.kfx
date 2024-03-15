package de.flapdoodle.kfx.controls.bettertable.events

class InsertRowState<T : Any>(
  private val defaultState: State<T>,
  private val context: EventContext<T>
) : StateWithContext<T>(context) {
  private var lastInsertRowRequest: TableEvent.RequestInsertRow<T>? = null

  override fun onEvent(event: TableEvent.RequestEvent<T>): State<T> {
    when (event) {
      is TableEvent.RequestInsertRow<T> -> {
        lastInsertRowRequest?.let {
          onTableEvent(it.undo())
        }
        lastInsertRowRequest = event
        onTableEvent(event.ok())
      }
      else -> {
        lastInsertRowRequest?.let {
          onTableEvent(it.undo())
        }
        return defaultState.onEvent(event)
      }
    }
    return this
  }
}