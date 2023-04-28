package de.flapdoodle.kfx.controls.table

import de.flapdoodle.kfx.controls.smarttable.Cursor
import de.flapdoodle.kfx.extensions.bindCss
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.WeakListChangeListener
import javafx.scene.control.Control
import javafx.scene.control.ScrollPane
import javafx.scene.control.SkinBase
import javafx.scene.layout.VBox

class SlimTable<T: Any>(
  internal val rows: ObservableList<T>,
  internal val columns: ObservableList<out Column<T, out Any>>
) : Control() {
  init {

    bindCss("slim-table")
  }

  private val skin = SmartTableSkin(this)

  private val rowsChangeListener = ListChangeListener<T> { skin.rowsChanged() }
  private val columnsChangeListener = ListChangeListener<Column<T, out Any>> { skin.columnsChanged() }

  init {
    isFocusTraversable = false
    rows.addListener(WeakListChangeListener(rowsChangeListener))
    columns.addListener(WeakListChangeListener(columnsChangeListener))

//    addClass(SmartTableStyles.smartTable)
    bindCss("smart-table")
  }

  override fun createDefaultSkin() = skin
  fun columns() = columns

  class SmartTableSkin<T : Any>(
    private val control: SlimTable<T>
  ) : SkinBase<SlimTable<T>>(control) {

    private var currentCursor: Cursor<T>? = null

    private val header = SlimHeader(control.columns)
//    private val rowsPane = SmartRows(control.rows, control.columns).apply {
//      VBox.setVgrow(this, Priority.ALWAYS)
//    }
    private val footer = SlimFooter(control.columns, header::columnWidthProperty)

    private val scroll = ScrollPane().apply {
//      style {
//        padding = box(0.px)
//      }
//      children.add(rowsPane)
      hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
//      content = rowsPane
    }

    private val all = VBox().apply {
      children.add(header)
      children.add(scroll)
      children.add(footer)
    }

    init {
      children.add(all)
      columnsChanged()
      rowsChanged()

//      control.addEventFilter(SmartEvents.TABLE) { event ->
//        when (event) {
//          is SmartEvents.ChangeCursor<out Any> -> {
//            event.consume()
//            if (event.cursor!=null && control.columns.contains(event.cursor.column)) {
//              println("cursor changed from $currentCursor to ${event.cursor}")
//              @Suppress("UNCHECKED_CAST")
//              currentCursor = event.cursor as Cursor<T>
//            } else {
//              println("cursor changed from $currentCursor to null")
//              currentCursor = null
//            }
//          }
//          is SmartEvents.MoveCursor -> {
//            event.consume()
//
//            currentCursor = currentCursor?.let {
//              val row = Math.min(Integer.max(0, (it.row + event.deltaRow)), control.rows.size - 1)
//              val colIndex = control.columns.indexOf(it.column)
//              if (colIndex != -1) {
//                val column = Math.min(Integer.max(0, colIndex + event.deltaColumn), control.columns.size - 1)
//                Cursor(control.columns[column], row)
//              } else it
//            }
//
//            //control.fireEvent(Events.C)
//            println("cursor changed to $currentCursor")
//            currentCursor?.let {
//              println("fire set-cursor to $currentCursor")
//              if (false) rowsPane.setCursor(it)
//              else  {
////                control.fireEventToChildren(SmartEvents.SetCursor(it))
//              }
//            }
//          }
//          else -> println("what? -> $event")
//        }
//      }
    }

    internal fun rowsChanged() {
//      rowsPane.rowsChanged()
      currentCursor?.let {
//        rowsPane.setCursor(it)
      }
    }

    internal fun columnsChanged() {
      header.columnsChanged()
//      rowsPane.columnsChanged()
      footer.columnsChanged()
      currentCursor?.let {
//        rowsPane.setCursor(it)
      }
    }

  }

}