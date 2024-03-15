package de.flapdoodle.kfx.controls.bettertable.events

class EditState<T : Any>(
  private val defaultState: State<T>,
  private val context: EventContext<T>
) : StateWithContext<T>(context) {
  override fun onEvent(event: TableEvent.RequestEvent<T>): State<T> {
    when (event) {
      is TableEvent.RequestEdit<T, out Any> -> {
        onTableEvent(TableEvent.StartEdit(event.row, event.column))
      }
      is TableEvent.CommitChange<T, out Any> -> {
        onChange(event.row, event.asCellChange())
        onTableEvent(event.stopEvent())
        return FocusState(defaultState, context).onEvent(TableEvent.RequestFocus(event.row, event.column))
      }
      is TableEvent.AbortChange<T, out Any> -> {
        onTableEvent(event.ok())
        return FocusState(defaultState, context).onEvent(TableEvent.RequestFocus(event.row, event.column))
      }
      else -> {
        println("EDIT MODE - Ignore: $event")
      }
    }
    return this
  }
}