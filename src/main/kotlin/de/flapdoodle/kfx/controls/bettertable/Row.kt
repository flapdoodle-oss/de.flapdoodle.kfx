package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.bindings.ObservableLists
import de.flapdoodle.kfx.extensions.PseudoClassWrapper
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.extensions.property
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.value.ObservableValue
import javafx.css.PseudoClass
import javafx.scene.control.Control
import javafx.scene.control.SkinBase
import javafx.scene.layout.HBox

class Row<T : Any>(
  internal val navigator: CellNavigator.RowNavigator<T>,
  internal val columns: ReadOnlyObjectProperty<List<Column<T, out Any>>>,
  internal val value: T,
//  internal val index: Int,
  internal val columnWidthProperties: (Column<T, out Any>) -> ObservableValue<Number>,
  internal val changeListener: CellChangeListener<T>
) : Control() {

  object Style {
    val Even = PseudoClassWrapper<Row<out Any>>(PseudoClass.getPseudoClass("even"))
  }

  private val skin = Skin(this)

  init {
    isFocusTraversable = false
    cssClassName("row")

//    if (index % 2 == 0) {
//      Style.Even.enable(this)
//    }
  }

  override fun createDefaultSkin(): javafx.scene.control.Skin<*> {
    return skin
  }

  fun setIndex(index: Int) {
    Style.Even.set(this, index % 2 == 0)
  }

  fun onTableEvent(event: TableEvent<T>) {
    skin.onTableEvent(event)
  }

  class Skin<T : Any>(
    private val control: Row<T>
  ) : SkinBase<Row<T>>(control) {
    private val rowContainer = HBox()

    init {
      children.add(rowContainer)

      ObservableLists.syncWith(control.columns, rowContainer.children) {
        cell(it, control.value, control.columnWidthProperties(it)).apply {
          property[Row::class] = control
          setNavigator(control.navigator.withColumn(it))
        }
      }
    }

    internal fun onTableEvent(event: TableEvent<T>) {
      when (event) {
        is TableEvent.Focus -> {
          if (event.row == control.value) {
            rowContainer.children.forEach {
              (it as Cell<T, out Any>).onTableEvent(event)
            }
          }
        }
        else -> {
          rowContainer.children.forEach {
            (it as Cell<T, out Any>).onTableEvent(event)
          }
        }
      }
    }

    private fun <C : Any> cell(c: Column<T, C>, value: T, width: ObservableValue<Number>): Cell<T, C> {
      return c.cell(value).apply {
        property[Column::class] = c
        changeListener { control.changeListener.onChange(control.value, CellChangeListener.Change(c, it)) }
        prefWidthProperty().bind(width)
      }
    }

  }

}
