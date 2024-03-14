package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.bindings.syncWith
import de.flapdoodle.kfx.extensions.cssClassName
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.scene.control.Control
import javafx.scene.control.SkinBase
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox

class Rows<T : Any>(
  private val rows: ReadOnlyObjectProperty<List<T>>,
  private val columns: ReadOnlyObjectProperty<List<Column<T, out Any>>>,
  private val eventListener: TableRequestEventListener<T>,
  private val columnWidthProperties: (Column<T, out Any>) -> ObservableValue<Number>
) : Control() {
  private val skin = Skin(this)

  init {
    isFocusTraversable = false
  }

  override fun createDefaultSkin() = skin

  fun onTableEvent(event: TableEvent.ResponseEvent<T>) {
    skin.onTableEvent(event)
  }

  class Skin<T : Any>(
    private val control: Rows<T>
  ) : SkinBase<Rows<T>>(control) {

    internal fun onTableEvent(event: TableEvent.ResponseEvent<T>) {
      rowPane.children.forEach {
        (it as Row<T>).onTableEvent(event)
      }
    }

    private val rowPane = VBox().apply {
      cssClassName("rows")
    }
    
    init {
      children.add(rowPane)

      rowPane.children.addListener(ListChangeListener {
        rowPane.children.forEachIndexed { index, node ->
          (node as Row<T>).setIndex(index)
        }
      })

      rowPane.children.syncWith(control.rows) {
        Row(control.eventListener, control.columns, it, control.columnWidthProperties)
      }

      rowPane.addEventFilter(MouseEvent.MOUSE_EXITED) {
        control.eventListener.fireEvent(TableEvent.MouseExitRows())
      }

//      ObservableLists.syncWithIndexed(control.rows, rowPane.children) { index, it ->
//        Row(control.columns, it, index, control.columnWidthProperties, control.changeListener)
//      }
    }
  }

}