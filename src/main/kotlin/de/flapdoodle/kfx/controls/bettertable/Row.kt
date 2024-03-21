package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.bindings.ObservableLists
import de.flapdoodle.kfx.controls.bettertable.events.TableEvent
import de.flapdoodle.kfx.controls.bettertable.events.TableRequestEventListener
import de.flapdoodle.kfx.extensions.*
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.Point2D
import javafx.scene.control.Control
import javafx.scene.control.SkinBase
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

class Row<T : Any>(
  internal val eventListener: TableRequestEventListener<T>,
  internal val columns: ReadOnlyObjectProperty<List<Column<T, out Any>>>,
  private val cellFactory: CellFactory<T>,
  internal val value: T,
//  internal val index: Int,
  internal val columnWidthProperties: (Column<T, out Any>) -> ObservableValue<Number>
) : Control() {

  private val skin = Skin(this)

  init {
    isFocusTraversable = false

//    if (index % 2 == 0) {
//      Style.Even.enable(this)
//    }
  }

  override fun createDefaultSkin(): javafx.scene.control.Skin<*> {
    return skin
  }

  fun setIndex(index: Int) {
    skin.setIndex(index)
  }

  fun onTableEvent(event: TableEvent.ResponseEvent<T>) {
    skin.onTableEvent(event)
  }

  fun columnSize(column: Column<T, out Any>) = skin.columnSize(column)

  class Skin<T : Any>(
    private val control: Row<T>
  ) : SkinBase<Row<T>>(control) {
    private val rowContainer = HBox()
    private val insertRowOnTopContainer = HBox()
    private val insertRowBottomContainer = HBox()
    private val all = VBox()

    init {
      insertRowOnTopContainer.cssClassName("row-insert-top")
      insertRowOnTopContainer.hide()
//      insertRowOnTopContainer.children.add(Button("+"))
      rowContainer.cssClassName("row")
      insertRowBottomContainer.cssClassName("row-insert-bottom")
      insertRowBottomContainer.hide()
//      insertRowBottomContainer.children.add(Button("+"))

      all.children.add(insertRowOnTopContainer)
      all.children.add(rowContainer)
      all.children.add(insertRowBottomContainer)
      children.add(all)

      all.addEventFilter(MouseEvent.ANY) {
        when (it.eventType) {
          MouseEvent.MOUSE_ENTERED -> control.eventListener.fireEvent(TableEvent.RequestInsertRow(control.value, guessPosition(it.localPosition, all.height)))
          MouseEvent.MOUSE_MOVED -> control.eventListener.fireEvent(TableEvent.RequestInsertRow(control.value, guessPosition(it.localPosition, all.height)))
//          MouseEvent.MOUSE_EXITED -> control.eventListener.fireEvent(TableEvent.AbortInsertRow(control.value))
        }
      }

      insertRowOnTopContainer.addEventHandler(MouseEvent.MOUSE_RELEASED) {
        println("insert new row above")
      }

      insertRowBottomContainer.addEventHandler(MouseEvent.MOUSE_RELEASED) {
        println("insert new row below")
      }



      ObservableLists.syncWith(control.columns, rowContainer.children) {
        cell(it, control.value, control.columnWidthProperties(it)).apply {
//          property[Row::class] = control
          setEventListener(control.eventListener)
        }
      }
    }

    private fun <C : Any> cell(c: Column<T, C>, value: T, width: ObservableValue<Number>): Cell<T, C> {
      return control.cellFactory.cell(c, value).apply {
        prefWidthProperty().bind(width)
//        onAttach { control.columnCellRegistry.register(c, this) }
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
      when (event) {
        is TableEvent.ToRow<T> -> {
          when (event) {
            is TableEvent.ShowInsertRow<T> -> {
              if (event.row == control.value) {
                when (event.position) {
                  TableEvent.InsertPosition.ABOVE -> {
                    insertRowOnTopContainer.show()
                    insertRowBottomContainer.hide()
                  }
                  TableEvent.InsertPosition.BELOW -> {
                    insertRowBottomContainer.show()
                    insertRowOnTopContainer.hide()
                  }
                }
              }
            }

            is TableEvent.HideInsertRow -> {
              if (event.row == control.value) {
                insertRowOnTopContainer.hide()
                insertRowBottomContainer.hide()
              }
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

    fun columnSize(column: Column<T, out Any>): ColumnSize {
      val cells = rowContainer.children.filter {
        (it as Cell<T, out Any>).column == column
      }
      require(cells.size==1) { "more or less than one match for: $column in ${rowContainer.children} -> $cells "}
      val cell = cells[0] as Cell<T, out Any>
      return cell.columnSize()
    }
  }
}
