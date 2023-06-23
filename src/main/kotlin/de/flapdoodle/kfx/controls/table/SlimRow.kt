package de.flapdoodle.kfx.controls.table

import de.flapdoodle.kfx.bindings.ObservableLists
import de.flapdoodle.kfx.extensions.PseudoClassWrapper
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.extensions.property
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.css.PseudoClass
import javafx.scene.control.Control
import javafx.scene.control.Skin
import javafx.scene.control.SkinBase
import javafx.scene.layout.HBox

class SlimRow<T : Any>(
  internal val columns: ObservableList<out Column<T, out Any>>,
  internal val value: T,
  internal val index: Int,
  internal val columnWidthProperties: (Column<T, out Any>) -> ObservableValue<Number>,
  internal val changeListener: CellChangeListener<T>
) : Control() {

  object Style {
    val Even = PseudoClassWrapper<SlimRow<out Any>>(PseudoClass.getPseudoClass("even"))
  }

  private val skin = SmartRowSkin(this)

  init {
    isFocusTraversable = false
    cssClassName("slim-row")

    if (index % 2 == 0) {
      Style.Even.enable(this)
    }
  }

  override fun createDefaultSkin(): Skin<*> {
    return skin
  }

  class SmartRowSkin<T : Any>(
    private val row: SlimRow<T>
  ) : SkinBase<SlimRow<T>>(row) {
    private val rowContainer = HBox()

    init {
      children.add(rowContainer)

      ObservableLists.syncWith(row.columns, rowContainer.children) {
        cell(it, row.value, row.columnWidthProperties(it))
      }
    }

    private fun <C : Any> cell(c: Column<T, C>, value: T, width: ObservableValue<Number>): SlimCell<T, C> {
      return c.cell(value).apply {
        property[Column::class] = c
        changeListener { row.changeListener.onChange(row.index, CellChangeListener.Change(c, it)) }
        prefWidthProperty().bind(width)
      }
    }

  }

}
