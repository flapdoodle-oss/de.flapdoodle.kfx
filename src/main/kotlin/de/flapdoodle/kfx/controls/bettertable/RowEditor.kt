package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.bindings.ObservableLists
import de.flapdoodle.kfx.controls.bettertable.events.TableEvent
import de.flapdoodle.kfx.controls.bettertable.events.TableRequestEventListener
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.extensions.hide
import de.flapdoodle.kfx.extensions.show
import de.flapdoodle.kfx.layout.StackLikeRegion
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.Region

class RowEditor<T : Any>(
  internal val eventListener: TableRequestEventListener<T>,
  internal val columns: ReadOnlyObjectProperty<List<Column<T, out Any>>>,
  private val cellFactory: CellFactory<T>,
  internal val value: T,
  internal val columnWidthProperties: (Column<T, out Any>) -> ObservableValue<Number>
) : StackLikeRegion() {

//  private val skin = Skin(this)
//  override fun createDefaultSkin() = skin


  private val rowContainer = HBox()

  init {
    rowContainer.cssClassName("row")
    children.add(rowContainer)

    ObservableLists.syncWith(columns, rowContainer.children) {
      editor(it, value, columnWidthProperties(it)).apply {
//          property[Row::class] = control
        // TODO move to constructor
        setEventListener(eventListener)
      }
    }
  }

  internal fun onTableEvent(event: TableEvent.ResponseEvent<T>) {
    println("row editor event: $event")
    when (event) {
      else -> {
        rowContainer.children.forEach {
          (it as RowEditorCell<T, out Any>).onTableEvent(event)
        }
      }
    }
  }

  private fun <C : Any> editor(column: Column<T, C>, row: T, width: ObservableValue<Number>): RowEditorCell<T, C> {
    val textField = RowEditorCell(column,row,column.property(row))
    textField.prefWidthProperty().bind(width)
    return textField
  }

  fun columnSize(column: Column<T, out Any>): ColumnSize {
    val cells = rowContainer.children.filter {
      (it as RowEditorCell<T, out Any>).column == column
    }
    require(cells.size==1) { "more or less than one match for: $column in ${rowContainer.children} -> $cells "}
    val cell = cells[0] as RowEditorCell<T, out Any>
    return cell.columnSize()
  }
}