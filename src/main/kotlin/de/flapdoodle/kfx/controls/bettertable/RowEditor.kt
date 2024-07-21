/*
 * Copyright (C) 2022
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.bindings.ObservableLists
import de.flapdoodle.kfx.controls.bettertable.events.TableEvent
import de.flapdoodle.kfx.controls.bettertable.events.TableRequestEventListener
import de.flapdoodle.kfx.controls.fields.FieldFactoryLookup
import de.flapdoodle.kfx.css.cssClassName
import de.flapdoodle.kfx.layout.StackLikeRegion
import javafx.beans.value.ObservableValue
import javafx.scene.layout.HBox

class RowEditor<T : Any>(
  internal val eventListener: TableRequestEventListener<T>,
  internal val columns: ObservableValue<List<Column<T, out Any>>>,
  internal var value: T,
  internal val columnWidthProperties: (Column<T, out Any>) -> ObservableValue<Number>,
  internal val fieldFactoryLookup: FieldFactoryLookup
) : StackLikeRegion() {

  private var stopped = false
  private val rowContainer = HBox()
  private val rowCellEventListenerDelegate: TableRequestEventListener<T> = TableRequestEventListener {
    if (!stopped) eventListener.fireEvent(it)
  }

  init {
    rowContainer.cssClassName("row","row-editor")
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
//    println("row editor event: $event")
    when (event) {
      is TableEvent.UpdateInsertRow<T> -> {
        value = event.row
        rowContainer.children.forEach {
          (it as RowEditorCell<T, out Any>).onTableEvent(event)
        }
      }
      is TableEvent.StopInsertRow<T> -> {
        stopped = true
      }
      else -> {
        rowContainer.children.forEach {
          (it as RowEditorCell<T, out Any>).onTableEvent(event)
        }
      }
    }
  }

  private fun <C : Any> editor(column: Column<T, C>, row: T, width: ObservableValue<Number>): RowEditorCell<T, C> {
    val textField = RowEditorCell(column,row,column.property.getter(row), rowCellEventListenerDelegate, fieldFactoryLookup)
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