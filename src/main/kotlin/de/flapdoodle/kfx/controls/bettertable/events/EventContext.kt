package de.flapdoodle.kfx.controls.bettertable.events

import de.flapdoodle.kfx.controls.bettertable.CellChangeListener
import de.flapdoodle.kfx.controls.bettertable.Column
import de.flapdoodle.kfx.controls.bettertable.TableChangeListener
import javafx.beans.property.ReadOnlyObjectProperty

data class EventContext<T : Any>(
  val rows: ReadOnlyObjectProperty<List<T>>,
  val columns: ReadOnlyObjectProperty<List<Column<T, out Any>>>,
  val changeListener: TableChangeListener<T>,
  val onTableEvent: (TableEvent.ResponseEvent<T>) -> Unit
)