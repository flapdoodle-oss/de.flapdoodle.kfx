package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.bindings.ObservableLists
import de.flapdoodle.kfx.controls.bettertable.events.TableEvent
import de.flapdoodle.kfx.controls.bettertable.events.TableRequestEventListener
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.extensions.hide
import de.flapdoodle.kfx.extensions.localPosition
import de.flapdoodle.kfx.extensions.show
import de.flapdoodle.kfx.layout.StackLikeRegion
import javafx.beans.value.ObservableValue
import javafx.geometry.Point2D
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

class Row<T : Any>(
  internal val eventListener: TableRequestEventListener<T>,
  internal val columns: ObservableValue<List<Column<T, out Any>>>,
  private val cellFactory: CellFactory<T>,
  internal val value: T,
//  internal val index: Int,
  internal val columnWidthProperties: (Column<T, out Any>) -> ObservableValue<Number>
) : StackLikeRegion() {

  private val insertRowOnTopContainer = HBox()
  private val rowOnTopContainer = HBox()
  private val rowContainer = HBox()
  private val rowBottomContainer = HBox()
  private val insertRowBottomContainer = HBox()
  private val all = VBox()

  private var rowEditor: RowEditor<T>? = null

  init {
    isFocusTraversable = false

    insertRowOnTopContainer.cssClassName("row-insert-top")
    insertRowOnTopContainer.hide()
//      insertRowOnTopContainer.children.add(Button("+"))
    rowContainer.cssClassName("row")
    insertRowBottomContainer.cssClassName("row-insert-bottom")
    insertRowBottomContainer.hide()
//      insertRowBottomContainer.children.add(Button("+"))

    all.children.add(insertRowOnTopContainer)
    all.children.add(rowOnTopContainer)
    all.children.add(rowContainer)
    all.children.add(rowBottomContainer)
    all.children.add(insertRowBottomContainer)
    children.add(all)

    all.addEventFilter(MouseEvent.ANY) {
      val position = guessPosition(it.localPosition, all.height)
      when (it.eventType) {
        MouseEvent.MOUSE_ENTERED -> {
          it.consume()
          eventListener.fireEvent(TableEvent.MayInsertRow(value, position))
        }
        MouseEvent.MOUSE_MOVED -> {
          it.consume()
          eventListener.fireEvent(TableEvent.MayInsertRow(value, position))
        }
      }
    }

    insertRowOnTopContainer.addEventHandler(MouseEvent.MOUSE_RELEASED) {
      it.consume()
      eventListener.fireEvent(TableEvent.RequestInsertRow(value, TableEvent.InsertPosition.ABOVE))
//      println("insert new row above")
    }
    insertRowOnTopContainer.addEventHandler(KeyEvent.KEY_RELEASED) {
      it.consume()
      if (it.code == KeyCode.INSERT) {
        eventListener.fireEvent(TableEvent.RequestInsertRow(value, TableEvent.InsertPosition.ABOVE))
      }
    }

    insertRowBottomContainer.addEventHandler(MouseEvent.MOUSE_RELEASED) {
      it.consume()
      eventListener.fireEvent(TableEvent.RequestInsertRow(value, TableEvent.InsertPosition.BELOW))
//      println("insert new row below")
    }
    insertRowBottomContainer.addEventHandler(KeyEvent.KEY_RELEASED) {
      it.consume()
      if (it.code == KeyCode.INSERT) {
        eventListener.fireEvent(TableEvent.RequestInsertRow(value, TableEvent.InsertPosition.BELOW))
      }
    }

    ObservableLists.syncWith(columns, rowContainer.children) {
      cell(it, value, columnWidthProperties(it)).apply {
//          property[Row::class] = control
        setEventListener(eventListener)
      }
    }
  }

  private fun <C : Any> cell(c: Column<T, C>, value: T, width: ObservableValue<Number>): Cell<T, C> {
    return cellFactory.cell(c, value).apply {
      prefWidthProperty().bind(width)
//        onAttach { columnCellRegistry.register(c, this) }
//          .onDetach { it?.unsubscribe() }
    }
  }

  private fun guessPosition(localPosition: Point2D, height: Double): TableEvent.InsertPosition {
    return if (localPosition.y < height / 2.0)
      TableEvent.InsertPosition.ABOVE
    else
      TableEvent.InsertPosition.BELOW
  }

  fun setIndex(index: Int) {
    Styles.Even.set(rowContainer, index % 2 == 0)
  }

  internal fun onTableEvent(event: TableEvent.ResponseEvent<T>) {
//    println("$value -> event: $event")
    when (event) {
      is TableEvent.ToRow<T> -> {
        when (event) {
          is TableEvent.ShowInsertRow<T> -> {
            if (event.row == value) {
              when (event.position) {
                TableEvent.InsertPosition.ABOVE -> {
                  insertRowOnTopContainer.show()
                  insertRowOnTopContainer.requestFocus()
                  insertRowBottomContainer.hide()
                }

                TableEvent.InsertPosition.BELOW -> {
                  insertRowBottomContainer.show()
                  insertRowBottomContainer.requestFocus()
                  insertRowOnTopContainer.hide()
                }
              }
            }
          }
          is TableEvent.InsertRow<T> -> {
            if (event.row == value) {
              val newEditor = RowEditor(eventListener,columns,event.emptyRow,columnWidthProperties)
              when (event.position) {
                TableEvent.InsertPosition.ABOVE -> {
                  rowOnTopContainer.children.add(newEditor)
                }
                TableEvent.InsertPosition.BELOW -> {
                  rowBottomContainer.children.add(newEditor)
                }
              }
              rowEditor = newEditor
            }
          }
          is TableEvent.UpdateInsertRow<T> -> {
            rowEditor?.onTableEvent(event)
          }
          is TableEvent.StopInsertRow<T> -> {
            rowEditor?.let {
              if (it.value!=event.row) {
                println("row editor does not match: ${it.value} != ${event.row}")
              }
              rowOnTopContainer.children.clear()
              rowBottomContainer.children.clear()
            }
            rowEditor = null
          }

          is TableEvent.HideInsertRow -> {
            if (event.row == value) {
              insertRowOnTopContainer.hide()
              insertRowBottomContainer.hide()
            }
          }
        }
      }

      else -> {
        rowEditor?.onTableEvent(event)
        rowContainer.children.forEach {
          (it as Cell<T, out Any>).onTableEvent(event)
        }
      }
    }
  }

  fun columnSize(column: Column<T, out Any>): ColumnSize {
    val cells = rowContainer.children.filter {
      (it as Cell<T, out Any>).column == column
    }
    require(cells.size == 1) { "more or less than one match for: $column in ${rowContainer.children} -> $cells " }
    val cell = cells[0] as Cell<T, out Any>
    return cell.columnSize()
  }
}
