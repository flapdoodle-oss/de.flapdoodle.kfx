package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.controls.bettertable.events.TableRequestEventListener

interface CellFactory<T: Any> {
  fun <C: Any> cell(column: Column<T, C>, row: T, eventListener: TableRequestEventListener<T>): Cell<T, C>

  fun andThen(action: (Column<T, out Any>, Cell<T, out Any>) -> Unit): CellFactory<T> {
    val that: CellFactory<T> = this
    return object : CellFactory<T> {
      override fun <C : Any> cell(column: Column<T, C>, row: T, eventListener: TableRequestEventListener<T>): Cell<T, C> {
        val cell = that.cell(column, row, eventListener)
        action(column, cell)
        return cell
      }
    }
  }

  class Default<T: Any> : CellFactory<T> {
    override fun <C : Any> cell(column: Column<T, C>, row: T, eventListener: TableRequestEventListener<T>): Cell<T, C> {
      return Cell(column, row, column.property.getter(row), eventListener)
    }
  }
}