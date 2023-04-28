package de.flapdoodle.kfx.controls.table

import de.flapdoodle.kfx.bindings.ObservableLists
import de.flapdoodle.kfx.controls.smarttable.Cursor
import de.flapdoodle.kfx.controls.smarttable.SmartRow
import de.flapdoodle.kfx.extensions.cssClassName
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.scene.control.Control
import javafx.scene.control.SkinBase
import javafx.scene.layout.VBox

class SlimRows<T : Any>(
  private val rows: ObservableList<T>,
  private val columns: ObservableList<out Column<T, out Any>>,
  private val columnWidthProperties: (Column<T, out Any>) -> ObservableValue<Number>
) : Control() {
  private val skin = SmartRowsSkin(this)

  init {
    cssClassName("slim-rows")
  }

  override fun createDefaultSkin() = skin
  internal fun rowsChanged() {
    skin.rowsChanged()
  }

  fun columnsChanged() {
    skin.columnsChanged()
  }

  internal fun setCursor(cursor: Cursor<T>) {
    skin.setCursor(cursor)
  }

  class SmartRowsSkin<T : Any>(
    private val control: SlimRows<T>
  ) : SkinBase<SlimRows<T>>(control) {
    private val rowPane = VBox()

    init {
      // TODO what?
      consumeMouseEvents(false)

      children.add(rowPane)

      ObservableLists.syncWithIndexed(control.rows, rowPane.children) { index, it ->
        SlimRow(control.columns, it, index, control.columnWidthProperties)
      }

      rowsChanged()
    }

    internal fun rowsChanged() {
//      rowPane.children.setAll(control.rows.mapIndexed { index, t ->
//        SlimRow(control.columns, t, index)
//      })
    }

    fun columnsChanged() {
//      rowPane.children.forEach {
//        (it as SmartRow<T>).columnsChanged()
//      }
    }

    internal fun setCursor(cursor: Cursor<T>) {
//      rowPane.children.forEach {
//        (it as SmartRow<T>).setCursor(cursor)
//      }
    }
  }

}