package de.flapdoodle.kfx.controls.table

import de.flapdoodle.kfx.bindings.ObservableLists
import de.flapdoodle.kfx.extensions.cssClassName
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.scene.control.Control
import javafx.scene.control.SkinBase
import javafx.scene.layout.VBox

class SlimRows<T : Any>(
  private val rows: ObservableList<T>,
  private val columns: ObservableList<out Column<T, out Any>>,
  private val columnWidthProperties: (Column<T, out Any>) -> ObservableValue<Number>,
  internal val changeListener: CellChangeListener<T>
) : Control() {
  private val skin = SmartRowsSkin(this)

  init {
    cssClassName("slim-rows")
  }

  override fun createDefaultSkin() = skin

  class SmartRowsSkin<T : Any>(
    private val control: SlimRows<T>
  ) : SkinBase<SlimRows<T>>(control) {
    private val rowPane = VBox()

    init {
      children.add(rowPane)

      ObservableLists.syncWithIndexed(control.rows, rowPane.children) { index, it ->
        SlimRow(control.columns, it, index, control.columnWidthProperties, control.changeListener)
      }
    }
  }

}