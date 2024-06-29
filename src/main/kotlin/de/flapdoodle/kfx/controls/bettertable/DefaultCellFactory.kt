package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.controls.bettertable.events.TableRequestEventListener
import de.flapdoodle.kfx.controls.fields.DefaultFieldFactoryLookup
import de.flapdoodle.kfx.controls.fields.FieldFactoryLookup

class DefaultCellFactory<T: Any>(
  val fieldFactoryLookup: FieldFactoryLookup = DefaultFieldFactoryLookup()
) : CellFactory<T> {
  override fun <C : Any> cell(column: Column<T, C>, row: T, eventListener: TableRequestEventListener<T>): Cell<T, C> {
    return Cell(column, row, column.property.getter(row), eventListener, fieldFactoryLookup)
  }
}