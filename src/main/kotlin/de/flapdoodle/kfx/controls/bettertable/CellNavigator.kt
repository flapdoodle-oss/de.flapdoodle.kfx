package de.flapdoodle.kfx.controls.bettertable

import javafx.beans.property.ReadOnlyObjectProperty
import javafx.event.Event
import javafx.event.EventType

class CellNavigator<T: Any>(
  val root: Table<T>,
  val rows: ReadOnlyObjectProperty<List<T>>,
  val columns: ReadOnlyObjectProperty<List<Column<T, out Any>>>,
  changeListener: CellChangeListener<T>
) {
  fun withRow(row: T): RowNavigator<T> {
    return RowNavigator(this, row)
  }

  data class RowNavigator<T: Any>(val parent: CellNavigator<T>, val row: T) {

    fun withColumn(column: Column<T, out Any>): ColumnNavigator<T> {
      return ColumnNavigator(this, column)
    }
  }

  data class ColumnNavigator<T: Any>(val parent: RowNavigator<T>, val column: Column<T, out Any>) {
    fun requestFocus() {
      println("request focus: ${parent.row}:$column")
      parent.parent.root.onTableEvent(TableEvent.Focus(parent.row, column))
    }

  }

  companion object {
//    val EVENT_TYPE = EventType<TableEvent<out Any>>("tableEvent")
//
//    sealed class TableEvent<T: Any>: Event(EVENT_TYPE) {
//      data class RequestFocus<T: Any>(val row: T, val column: Column<T, out Any>): TableEvent<T>()
//    }
  }

}