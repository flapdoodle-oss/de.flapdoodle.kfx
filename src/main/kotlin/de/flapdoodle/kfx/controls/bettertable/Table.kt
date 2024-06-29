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
import de.flapdoodle.kfx.collections.Diff
import de.flapdoodle.kfx.controls.bettertable.events.*
import de.flapdoodle.kfx.controls.fields.DefaultFieldFactoryLookup
import de.flapdoodle.kfx.controls.fields.FieldFactoryLookup
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.extensions.onBindToScene
import de.flapdoodle.kfx.layout.StackLikeRegion
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.kfx.logging.Logging
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.HPos
import javafx.scene.control.ScrollPane
import javafx.util.Subscription

class Table<T: Any>(
  internal val rows: ObservableValue<List<T>>,
  internal val columns: ReadOnlyObjectProperty<List<Column<T, out Any>>>,
  internal val changeListener: TableChangeListener<T>,
  headerColumnFactory: HeaderColumnFactory<T> = HeaderColumnFactory.Default(),
  fieldFactoryLookup: FieldFactoryLookup = DefaultFieldFactoryLookup(),
  cellFactory: CellFactory<T> = DefaultCellFactory(fieldFactoryLookup),
  footerColumnFactory: FooterColumnFactory<T>? = FooterColumnFactory.Default(),
  stateFactory: (EventContext<T>) -> State<T> = { DefaultState(it) }
) : StackLikeRegion() {

  private val logger = Logging.logger(Table::class)

  private val _rows: SimpleObjectProperty<List<T>> = SimpleObjectProperty(emptyList())
  private val _columns: SimpleObjectProperty<List<Column<T, out Any>>> = SimpleObjectProperty(emptyList())

  private val eventContext = EventContext(_rows, _columns, changeListener) {
    onTableEvent(it)
  }
  private val eventListener = StateEventListener(stateFactory(eventContext))

  private val header = Header(_columns, eventListener, headerColumnFactory)
  private val __rows = Rows(_rows, _columns, cellFactory, eventListener, header::columnWidthProperty, fieldFactoryLookup)
  private val footer = Footer(_columns, header::columnWidthProperty, footerColumnFactory)

  private val scroll = ScrollPane().apply {
    hbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
    vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
    isFocusTraversable = false
    isPannable = true
    isFitToHeight = true
  }

  private val rowsWrapper = ScrollPane().apply {
    cssClassName("rows-scroll-pane")

    hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
    isFocusTraversable = false
    isPannable = true
    isFitToHeight = true
    content = __rows
  }

  init {
    isFocusTraversable = false
    bindCss("better-table")
    val content = WeightGridPane().apply {
      setRowWeight(0, 0.0)
      setRowWeight(1, 1.0)
      setRowWeight(2, 0.001)
      setRowWeight(3, 0.0)
    }

    WeightGridPane.setPosition(header, 0,0, HPos.CENTER)
    WeightGridPane.setPosition(rowsWrapper,0,1, HPos.CENTER)
    WeightGridPane.setPosition(footer, 0,3, HPos.CENTER)
    content.children.add(header)
    content.children.add(rowsWrapper)
    content.children.add(footer)

    scroll.content = content

    children.add(scroll)

    _rows.addListener { observable, oldValue, newValue ->
      if (newValue.isEmpty()) {
        eventListener.fireEvent(TableEvent.EmptyRows())
      } else {
        if (oldValue.isEmpty()) {
          eventListener.fireEvent(TableEvent.HasRows())
        }
      }
    }

    onBindToScene {
      val syncColumns = _columns.syncWith(columns) { it }
      val syncRows = _rows.syncWith(rows) { it }
      val resizeNewColumns = columns.subscribe { old, new ->
        Diff.between(old, new).added.forEach {
          eventListener.fireEvent(TableEvent.RequestResizeColumn(it))
        }
      }
      val resizeAllColumns = _rows.subscribe { old, new ->
        if (old.isEmpty() && new.isNotEmpty()) {
          _columns.value.forEach {
            eventListener.fireEvent(TableEvent.RequestResizeColumn(it))
          }
        }
      }

      if (_rows.value.isEmpty()) {
        eventListener.fireEvent(TableEvent.EmptyRows())
      }

      syncColumns
        .and(syncRows)
        .and(resizeNewColumns)
        .and(resizeAllColumns)
    }

    // TODO onBindToScene wird von oben nach unten propagiert, kann man das beheben?
    __rows.onBindToScene {
      _columns.value.forEach {
        eventListener.fireEvent(TableEvent.RequestResizeColumn(it))
      }
      Subscription { /* noop */ }
    }

  }

  internal fun onTableEvent(event: TableEvent.ResponseEvent<T>) {
    logger.debug { "table event: $event" }
    when (event) {
      is TableEvent.ResizeColumn<T, out Any> -> {
        val columnSize = __rows.columnSize(event.column)
//        val columnSize = _rows.preferredColumnSize(event.column)
        header.setColumnSize(event.column, columnSize)
      }
      is TableEvent.FocusTable<T> -> {
        requestFocus()
      }
      else -> {
        __rows.onTableEvent(event)
      }
    }
  }

//  override fun layoutChildren() {
//    val contentWidth = width - insets.left - insets.right
//    val contentHeight = height - insets.top - insets.bottom
//    layoutInArea(scroll, insets.left, insets.top, contentWidth, contentHeight, baselineOffset, HPos.CENTER, VPos.CENTER)
//  }
}