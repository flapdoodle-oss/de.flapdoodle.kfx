package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.bindings.ObservableLists
import de.flapdoodle.kfx.bindings.syncWith
import de.flapdoodle.kfx.extensions.cssClassName
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.control.Control
import javafx.scene.control.SkinBase
import javafx.scene.layout.VBox

class Rows<T : Any>(
  private val rows: ReadOnlyObjectProperty<List<T>>,
  private val columns: ReadOnlyObjectProperty<List<Column<T, out Any>>>,
  private val columnWidthProperties: (Column<T, out Any>) -> ObservableValue<Number>,
  internal val changeListener: CellChangeListener<T>
) : Control() {
  private val skin = Skin(this)

  init {
    cssClassName("rows")
    isFocusTraversable = false
  }

  override fun createDefaultSkin() = skin

  class Skin<T : Any>(
    private val control: Rows<T>
  ) : SkinBase<Rows<T>>(control) {
    private val rowPane = VBox()

    init {
      children.add(rowPane)

      rowPane.children.addListener(ListChangeListener {
        rowPane.children.forEachIndexed { index, node ->
          (node as Row<T>).setIndex(index)
        }
      })

      rowPane.children.syncWith(control.rows) {
        Row(control.columns, it, control.columnWidthProperties, control.changeListener)
      }

//      ObservableLists.syncWithIndexed(control.rows, rowPane.children) { index, it ->
//        Row(control.columns, it, index, control.columnWidthProperties, control.changeListener)
//      }
    }
  }

}