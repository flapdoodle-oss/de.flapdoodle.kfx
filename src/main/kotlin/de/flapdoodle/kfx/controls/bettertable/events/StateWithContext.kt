package de.flapdoodle.kfx.controls.bettertable.events

import de.flapdoodle.kfx.controls.bettertable.TableChangeListener

abstract class StateWithContext<T: Any>(
  private val context: EventContext<T>
) : State<T> {
  protected fun onTableEvent(event: TableEvent.ResponseEvent<T>) {
    context.onTableEvent(event)
  }

  protected fun changeCell(row: T, change: TableChangeListener.CellChange<T, out Any>): T {
    return context.changeListener.changeCell(row, change)
  }

  protected fun changeCellAndUpdateRow(row: T, change: TableChangeListener.CellChange<T, out Any>): T {
    val changed = context.changeListener.changeCell(row, change)
    context.changeListener.updateRow(row, changed)
    return changed
  }

  protected fun insertRow(index: Int, row: T): Boolean {
    return context.changeListener.insertRow(index, row)
  }

  protected fun removeRow(row: T) {
    context.changeListener.removeRow(row)
  }
}