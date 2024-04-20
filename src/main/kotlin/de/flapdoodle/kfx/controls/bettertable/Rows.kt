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

import de.flapdoodle.kfx.bindings.syncWith
import de.flapdoodle.kfx.controls.bettertable.events.TableEvent
import de.flapdoodle.kfx.controls.bettertable.events.TableRequestEventListener
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.layout.StackLikeRegion
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import kotlin.math.max

class Rows<T : Any>(
  private val rows: ObservableValue<List<T>>,
  private val columns: ObservableValue<List<Column<T, out Any>>>,
  private val cellFactory: CellFactory<T>,
  private val eventListener: TableRequestEventListener<T>,
  private val columnWidthProperties: (Column<T, out Any>) -> ObservableValue<Number>
) : StackLikeRegion() {

  private var rowEditor: RowEditor<T>? = null

  private val insertRowPane = VBox().apply {
    cssClassName("rows")
  }

  private val rowPane = VBox().apply {
    cssClassName("rows")
  }

  val all = VBox()

  init {
    isFocusTraversable = false

    rowPane.children.syncWith(rows) {
      Row(eventListener, columns, cellFactory, it, columnWidthProperties)
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
      eventListener.fireEvent(TableEvent.MouseExitRows())
    }
  }

  internal fun onTableEvent(event: TableEvent.ResponseEvent<T>) {
    when (event) {
      is TableEvent.InsertFirstRow<T> -> {
        require(rowEditor == null) { "rowEditor already set: $rowEditor" }
        val newEditor = RowEditor(eventListener, columns, event.row, columnWidthProperties)
        rowEditor = newEditor
        insertRowPane.children.add(newEditor)
      }
      is TableEvent.HideInsertFirstRow<T> -> {
        val oldEditor = rowEditor
        if (oldEditor != null) {
          insertRowPane.children.remove(oldEditor)
        }
        rowEditor = null
      }
      is TableEvent.StopInsertRow<T> -> {
        val oldEditor = rowEditor
        if (oldEditor != null && oldEditor.value==event.row) {
          insertRowPane.children.remove(oldEditor)
          rowEditor = null
        } else {
          rowPane.children.forEach {
            (it as Row<T>).onTableEvent(event)
          }
        }
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
    val rowEditorSizes = rowEditor?.let { listOf(it.columnSize(column)) } ?: emptyList()

    return (rowSizes + rowEditorSizes).fold(ColumnSize(0.0, 0.0)) { l, r ->
      ColumnSize(max(l.min, r.min), max(l.preferred, r.preferred))
    }
  }
}