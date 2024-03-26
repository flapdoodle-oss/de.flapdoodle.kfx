package de.flapdoodle.kfx.controls.bettertable

import javafx.scene.control.Label
import javafx.scene.text.TextAlignment

fun interface HeaderColumnFactory<T : Any> {
  fun headerColumn(column: Column<T, out Any>): HeaderColumn<T>

  fun andThen(action: (Column<T, out Any>, HeaderColumn<T>) -> Unit): HeaderColumnFactory<T> {
    val that: HeaderColumnFactory<T> = this
    return HeaderColumnFactory<T> {
      val headerColumn = that.headerColumn(it)
      action(it, headerColumn)
      headerColumn
    }
  }

  class Default<T : Any> : HeaderColumnFactory<T> {
    override fun headerColumn(column: Column<T, out Any>): HeaderColumn<T> {
      return HeaderColumn(column).apply {
        setContent(Label(column.label))
      }
    }
  }
}