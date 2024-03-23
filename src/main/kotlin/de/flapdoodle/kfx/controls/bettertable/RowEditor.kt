package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.bindings.ObservableLists
import de.flapdoodle.kfx.controls.bettertable.events.TableRequestEventListener
import de.flapdoodle.kfx.extensions.cssClassName
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.control.Control
import javafx.scene.control.Skin
import javafx.scene.control.SkinBase
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.Region

class RowEditor<T : Any>(
  internal val eventListener: TableRequestEventListener<T>,
  internal val columns: ReadOnlyObjectProperty<List<Column<T, out Any>>>,
  private val cellFactory: CellFactory<T>,
  internal val value: T,
  internal val columnWidthProperties: (Column<T, out Any>) -> ObservableValue<Number>
) : Region() {

//  private val skin = Skin(this)
//  override fun createDefaultSkin() = skin

  
  private val rowContainer = HBox()
  init {
    rowContainer.cssClassName("row")
    children.add(rowContainer)

    ObservableLists.syncWith(columns, rowContainer.children) {
      editor(it, value, columnWidthProperties(it)).apply {
//          property[Row::class] = control
//        setEventListener(eventListener)
      }
    }
  }

  private fun <C : Any> editor(c: Column<T, C>, value: T, width: ObservableValue<Number>): TextField {
    val textField = Cells.createTextField(c.property(value), c.converter, {},  {})
    textField.prefWidthProperty().bind(width)
    return textField
//    return cellFactory.cell(c, value).apply {
//      prefWidthProperty().bind(width)
//    }
  }

  override fun layoutChildren() {
    layoutInArea(rowContainer,insets.left, insets.top, width-insets.left-insets.right, height-insets.top-insets.bottom,  -1.0, HPos.LEFT, VPos.TOP)
  }


//  class Skin<T : Any>(
//    private val control: RowEditor<T>
//  ) : SkinBase<RowEditor<T>>(control) {
//  }
}