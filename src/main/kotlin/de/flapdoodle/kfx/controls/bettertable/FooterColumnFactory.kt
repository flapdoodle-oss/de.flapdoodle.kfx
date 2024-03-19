package de.flapdoodle.kfx.controls.bettertable

import javafx.scene.control.Label

fun interface FooterColumnFactory<T: Any> {
  fun footerColumn(column: Column<T, out Any>): FooterColumn<T>

  fun andThen(action: (Column<T, out Any>, FooterColumn<T>) -> Unit): FooterColumnFactory<T> {
    val that: FooterColumnFactory<T> = this
    return FooterColumnFactory<T> {
      val footerColumn = that.footerColumn(it)
      action(it, footerColumn)
      footerColumn
    }
  }

  class Default<T : Any> : FooterColumnFactory<T> {
    override fun footerColumn(column: Column<T, out Any>): FooterColumn<T> {
      return FooterColumn(column).apply {
        setContent(Label(column.label))
      }
    }
  }
}