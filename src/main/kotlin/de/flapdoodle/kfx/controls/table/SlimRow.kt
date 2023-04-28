package de.flapdoodle.kfx.controls.table

import de.flapdoodle.kfx.bindings.ObservableLists
import de.flapdoodle.kfx.controls.smarttable.*
import de.flapdoodle.kfx.extensions.PseudoClassWrapper
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.extensions.property
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.css.PseudoClass
import javafx.scene.control.Control
import javafx.scene.control.Skin
import javafx.scene.control.SkinBase
import javafx.scene.layout.HBox

class SlimRow<T : Any>(
  internal val columns: ObservableList<out Column<T, out Any>>,
  internal val value: T,
  internal val index: Int,
  internal val columnWidthProperties: (Column<T, out Any>) -> ObservableValue<Number>,
  internal val changeListener: CellChangeListener<T>
) : Control() {

  object Style {
    val Even = PseudoClassWrapper<SlimRow<out Any>>(PseudoClass.getPseudoClass("even"))
  }

  private val skin = SmartRowSkin(this)

  init {
    isFocusTraversable = false
    cssClassName("slim-row")
    if (index % 2 == 0) {
      Style.Even.enable(this)
    }

    if (index % 2 == 0) {
//      addClass(Stylesheet.even)
    } else {
//      addClass(Stylesheet.odd)
    }
  }

  override fun createDefaultSkin(): Skin<*> {
    return skin
  }

  fun columnsChanged() {
    skin.columnsChanged()
  }

  internal fun setCursor(cursor: Cursor<T>) {
    skin.setCursor(cursor)
  }


  class SmartRowSkin<T : Any>(
    private val row: SlimRow<T>
  ) : SkinBase<SlimRow<T>>(row) {
    private val rowContainer = HBox()

    fun columnsChanged() {
      //rowContainer.children.setAll(row.columns.map { cell(it, row.value) })
    }

    init {
      children.add(rowContainer)

      ObservableLists.syncWith(row.columns, rowContainer.children) {
        cell(it, row.value, row.columnWidthProperties(it))
      }
      
      columnsChanged()

      row.addEventFilter(SmartEvents.ALL) { event ->
        when (event) {
          is SmartEvents.EditDone -> {
            event.consume()
            println("Row: EditDone in ${event.cell}")
            row.fireEvent(SmartEvents.MoveCursor(deltaRow = 1))
          }
          is SmartEvents.CellFocused -> {
            event.consume()
            println("Cell focused: ${event.cell}")

            val column = event.cell.property[SmartColumn::class]
            val matchingColumn = row.columns.find { it == column }
            require(matchingColumn!=null) {"column not found: $column -> ${row.columns}"}
//            row.fireEvent(SmartEvents.ChangeCursor(Cursor(matchingColumn, row.index)))
          }
          is SmartEvents.CellBlur -> {
            event.consume()
            println("Cell blur: ${event.cell}")
//            row.fireEvent(SmartEvents.ChangeCursor<T>(null))
          }
          is SmartEvents.SetCursor<out Any> -> {
            setCursor(event.cursor as Cursor<T>)
          }
          else -> println("$event")
        }
//        event.consume()
      }
    }

    fun setCursor(cursor: Cursor<T>) {
      if (cursor.row==row.index) {
        println("set cursor ${cursor} matches")
        val cell = rowContainer.children.find {
          val cellColumn = it.property[SmartColumn::class]
          println("$cellColumn ? ${cursor.column} -> ${it.properties}")
          cellColumn == cursor.column
        }
        println("request focus for ${cursor} -> $cell (${cell?.isFocused})")
        if (cell!=null && !cell.isFocused) {
          println("do it for ${cell}")
          cell.requestFocus()
        }
      }
    }

    private fun <C : Any> cell(c: Column<T, C>, value: T, width: ObservableValue<Number>): SlimCell<T, C> {
      return c.cell(value).apply {
        property[Column::class] = c
        changeListener { row.changeListener.onChange(row.index, CellChangeListener.Change(c, it)) }
        prefWidthProperty().bind(width)
      }
    }

  }

}