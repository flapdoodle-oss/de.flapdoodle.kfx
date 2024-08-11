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
package de.flapdoodle.kfx.layout.grid

import de.flapdoodle.kfx.collections.Diff
import de.flapdoodle.kfx.extensions.withAnchors
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.layout.AnchorPane

class GridTable<T : Any, I : Any>(
  model: ReadOnlyObjectProperty<List<T>>,
  private val indexOf: (T) -> I,
  private val columns: List<Column<T>>,
  private val headerFactories: List<HeaderFooterFactory<T>> = emptyList(),
  private val footerFactories: List<HeaderFooterFactory<T>> = emptyList(),
) : AnchorPane() {

  constructor(
    model: ReadOnlyObjectProperty<List<T>>,
    indexOf: (T) -> I,
    columns: List<Column<T>>,
    headerFactory: HeaderFooterFactory<T>,
    footerFactory: HeaderFooterFactory<T>? = null,
  ) : this(model, indexOf, columns, listOf(headerFactory), if (footerFactory!=null) listOf(footerFactory) else emptyList())

  init {
    require(columns.toSet().size == columns.size) { "some columns are added more than once: $columns" }
  }

  private val grid = GridPane()
  private val columnIndex = columns.mapIndexed { index, column -> column to index }.toMap()

  private var headerNodes: List<Map<Node, Span>> = emptyList()
  private var rows = emptyList<Row<T, I>>()
  private var footerNodes: List<Map<Node, Span>> = emptyList()

  fun verticalSpace() = grid.verticalSpace
  fun horizontalSpace() = grid.horizontalSpace

  init {
    grid.withAnchors(all = 0.0)
    children.add(grid)

    update(emptyList(), model.value)
    model.addListener { _, oldValue, newValue ->
      update(oldValue, newValue)
    }
  }

  private fun update(old: List<T>, new: List<T>) {
    val newHeaderNodes = headerFactories.map { it.nodesOf(new, columns) }
    val newFooterNodes = footerFactories.map { it.nodesOf(new, columns) }

//    require(newHeaderNodes==null || newHeaderNodes.size == columns.size) {"headerNodes size does not match column size"}
//    require(newFooterNodes==null || newFooterNodes.size == columns.size) {"footerNodes size does not match column size"}

    val changes = Diff.between(old, new, indexOf)

    val removedIds = changes.removed.map { indexOf(it) }.toSet()
    val modified = changes.modified.map { indexOf(it.second) to it.second }.toMap()
    val addedIds = changes.added.map { indexOf(it) }.toSet()

    val (nodesToRemove, rowsWithoutRemoved) = rows.partition { removedIds.contains(it.index) }

    val newRows = new.filter { addedIds.contains(indexOf(it)) }.map {
      Row(indexOf(it), columns.map { c ->
        c.cellFactory?.let { f -> f.cellOf(it) }
      })
    }

    val allRows = rowsWithoutRemoved + newRows
    val offset = newHeaderNodes.size

    allRows.forEachIndexed { rowIndex, row ->
      row.nodes.forEachIndexed { columnIndex, nodeAndUpdate ->
        if (nodeAndUpdate != null) {
          val column = columns[columnIndex]
          GridPane.setPosition(nodeAndUpdate.node, columnIndex, rowIndex + offset, column.horizontalPosition, column.verticalPosition)
        }
      }
    }

    headerNodes.forEach { grid.children.removeAll(it.keys) }
    newHeaderNodes.forEachIndexed { index, map ->
      map.forEach { (header, span) ->
        val start = columnIndex[span.start]!!
        val end = columnIndex[span.end]!!
        val pos = Pos(column = start, columnSpan = end - start + 1, row = index)
        GridPane.setPosition(header, pos, span.horizontalPosition, span.verticalPosition)
        grid.children.add(header)
      }
    }
    headerNodes = newHeaderNodes

    nodesToRemove.forEach { row -> row.nodes.forEach { if (it != null) grid.children.removeAll(it.node) } }
    rowsWithoutRemoved.forEach { row ->
      val changedValue = modified[row.index]
      if (changedValue != null) {
        row.nodes.forEach { it: TableCell<T, out Node>? ->
          it?.updateCell(changedValue)
        }
      }
    }
    newRows.forEach { row -> row.nodes.forEach { if (it != null) grid.children.add(it.node) } }
    rows = allRows

    footerNodes.forEach { grid.children.removeAll(it.keys) }
    newFooterNodes.forEachIndexed { index, map ->
      map.forEach { (footer, span) ->
        val start = columnIndex[span.start]!!
        val end = columnIndex[span.end]!!
        val pos = Pos(column = start, columnSpan = end - start + 1, row = allRows.size + offset + index)
        GridPane.setPosition(footer, pos, span.horizontalPosition, span.verticalPosition)
        grid.children.add(footer)
      }
    }
    footerNodes = newFooterNodes
  }

  fun interface HeaderFooterFactory<T: Any> {
    fun nodesOf(values: List<T>, columns: List<Column<T>>): Map<Node, Span>
  }
  
  fun interface CellFactory<T: Any, N: Node> {
    fun cellOf(value: T): TableCell<T, out N>
  }

  data class Column<T : Any>(
    val weight: Double = 1.0,
    val cellFactory: CellFactory<T, out Node>? = null,
    val horizontalPosition: HPos? = null,
    val verticalPosition: VPos? = null
  )

  data class Span(
    val start: Column<out Any>,
    val end: Column<out Any> = start,
    val horizontalPosition: HPos? = null,
    val verticalPosition: VPos? = null
  )

  private data class Row<T : Any, I : Any>(
    val index: I,
    val nodes: List<TableCell<T, out Node>?>
  )
}