package de.flapdoodle.kfx.controls.bettertable.events

import javafx.scene.control.Tab

class EditState<T : Any>(
  private val defaultState: State<T>,
  private val context: EventContext<T>
) : StateWithContext<T>(context) {
  private var lastEdit: TableEvent.RequestEdit<T, out Any>? = null

  override fun onEvent(event: TableEvent.RequestEvent<T>): State<T> {
    when (event) {
      is TableEvent.RequestEdit<T, out Any> -> {
        lastEdit?.let {
          onTableEvent(TableEvent.StopEdit(it.row, it.column))
        }
        lastEdit = event
        onTableEvent(TableEvent.StartEdit(event.row, event.column))
      }
      is TableEvent.CommitChange<T, out Any> -> {
        onTableEvent(event.stopEvent())
        val changed = onChange(event.row, event.asCellChange())
        return FocusState(defaultState, context).onEvent(TableEvent.RequestFocus(changed, event.column))
      }
      is TableEvent.AbortChange<T, out Any> -> {
        onTableEvent(event.ok())
        return FocusState(defaultState, context).onEvent(TableEvent.RequestFocus(event.row, event.column))
      }
      is TableEvent.EditLostFocus<T, out Any> -> {
        onTableEvent(event.ok())
        return FocusState(defaultState, context).onEvent(TableEvent.RequestFocus(event.row, event.column))
      }
      else -> {
//        println("EDIT MODE - Ignore: $event")
      }
    }
    return this
  }
}