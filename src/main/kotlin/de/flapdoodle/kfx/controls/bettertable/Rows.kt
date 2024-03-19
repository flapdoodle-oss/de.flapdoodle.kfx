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

  class Skin<T : Any>(
    private val control: Rows<T>
  ) : SkinBase<Rows<T>>(control) {

    internal fun onTableEvent(event: TableEvent.ResponseEvent<T>) {
      rowPane.children.forEach {
        (it as Row<T>).onTableEvent(event)
      }
    }

    fun columnSize(column: Column<T, out Any>): ColumnSize {
      return rowPane.children.map {
        (it as Row<T>).columnSize(column)
      }.fold(ColumnSize(0.0, 0.0)) { l, r ->
        ColumnSize(max(l.min, r.min), max(l.preferred, r.preferred))
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
        Row(control.eventListener, control.columns, control.cellFactory, it, control.columnWidthProperties)
      }

      rowPane.addEventFilter(MouseEvent.MOUSE_EXITED) {
        control.eventListener.fireEvent(TableEvent.MouseExitRows())
      }
    }
  }

}