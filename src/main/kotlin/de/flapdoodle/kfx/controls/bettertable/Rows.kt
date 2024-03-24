package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.bindings.syncWith
import de.flapdoodle.kfx.controls.bettertable.events.TableEvent
import de.flapdoodle.kfx.controls.bettertable.events.TableRequestEventListener
import de.flapdoodle.kfx.extensions.cssClassName
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.scene.control.Control
import javafx.scene.control.SkinBase
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import kotlin.math.max

class Rows<T : Any>(
  private val rows: ReadOnlyObjectProperty<List<T>>,
  private val columns: ReadOnlyObjectProperty<List<Column<T, out Any>>>,
  private val cellFactory: CellFactory<T>,
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

  fun columnSize(column: Column<T, out Any>) = skin.columnSize(column)

  override fun layoutChildren() {
    super.layoutChildren()
  }

  class Skin<T : Any>(
    private val control: Rows<T>
  ) : SkinBase<Rows<T>>(control) {

    private var rowEditor: RowEditor<T>? = null

    internal fun onTableEvent(event: TableEvent.ResponseEvent<T>) {
      when (event) {
        is TableEvent.InsertFirstRow<T> -> {
          require(rowEditor == null) { "rowEditor already set: $rowEditor" }
          val newEditor = RowEditor(control.eventListener, control.columns, control.cellFactory, event.row, control.columnWidthProperties)
          rowEditor = newEditor
          insertRowPane.children.add(newEditor)
        }
        else -> {
          rowEditor?.onTableEvent(event)
          rowPane.children.forEach {
            (it as Row<T>).onTableEvent(event)
          }
        }
      }
    }

    fun columnSize(column: Column<T, out Any>): ColumnSize {
      val rowSizes = rowPane.children.map {
        (it as Row<T>).columnSize(column)
      }
      val rowEditorSizes =  rowEditor?.let { listOf(it.columnSize(column)) } ?: emptyList()

      return (rowSizes + rowEditorSizes).fold(ColumnSize(0.0, 0.0)) { l, r ->
        ColumnSize(max(l.min, r.min), max(l.preferred, r.preferred))
      }
    }

    private val insertRowPane = VBox().apply {
      cssClassName("rows")
    }

    private val rowPane = VBox().apply {
      cssClassName("rows")
    }

    val all=VBox()

    init {
      rowPane.children.syncWith(control.rows) {
        Row(control.eventListener, control.columns, control.cellFactory, it, control.columnWidthProperties)
      }

      rowPane.children.addListener(ListChangeListener {
        rowPane.children.forEachIndexed { index, node ->
          (node as Row<T>).setIndex(index)
        }
      })

      all.children.add(insertRowPane)
      all.children.add(rowPane)
      children.add(all)

      rowPane.addEventFilter(MouseEvent.MOUSE_EXITED) {
        control.eventListener.fireEvent(TableEvent.MouseExitRows())
      }

//      insertRowPane.children.add(RowEditor(control.eventListener, control.columns, control.cellFactory, it, control.columnWidthProperties))
    }
  }

}