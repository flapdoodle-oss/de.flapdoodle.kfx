package de.flapdoodle.kfx.controls.bettertable.events

import de.flapdoodle.kfx.controls.bettertable.CellChangeListener

abstract class StateWithContext<T: Any>(
  private val context: EventContext<T>
) : State<T> {
  protected fun onTableEvent(event: TableEvent.ResponseEvent<T>) {
    context.onTableEvent(event)
  }

  protected fun onChange(row: T, change: CellChangeListener.Change<T, out Any>) {
    context.changeListener.onChange(row, change)
  }
}