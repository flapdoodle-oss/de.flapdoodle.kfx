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

class WeightGridTable<T : Any, I : Any>(
  model: ReadOnlyObjectProperty<List<T>>,
  private val indexOf: (T) -> I,
  private val columns: List<Column<T>>,
  private val headerFactory: HeaderFooterNodeFactory<T>? = null,
  private val footerFactory: HeaderFooterNodeFactory<T>? = null,
) : AnchorPane() {
  init {
    require(columns.toSet().size == columns.size) { "some columns are added more than once: $columns" }
  }

  private val grid = WeightGridPane()

  private var headerNodes: Map<Column<T>, Node>? = null
  private var rows = emptyList<Row<T, I>>()
  private var footerNodes: Map<Column<T>, Node>? = null

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
    val newHeaderNodes = headerFactory?.nodesOf(new, columns)
    val newFooterNodes = footerFactory?.nodesOf(new, columns)

//    require(newHeaderNodes==null || newHeaderNodes.size == columns.size) {"headerNodes size does not match column size"}
//    require(newFooterNodes==null || newFooterNodes.size == columns.size) {"footerNodes size does not match column size"}

    val changes = Diff.between(old, new, indexOf)

    val removedIds = changes.removed.map { indexOf(it) }.toSet()
    val modified = changes.modified.map { indexOf(it.second) to it.second }.toMap()
    val addedIds = changes.added.map { indexOf(it) }.toSet()

    val (nodesToRemove, rowsWithoutRemoved) = rows.partition { removedIds.contains(it.index) }

    val newRows = new.filter { addedIds.contains(indexOf(it)) }.map {
      Row(indexOf(it), columns.map { c ->
        require(c.nodeFactory==null || c.cellFactory==null) {"nodeFactory AND cellFactory set: $c"}
        c.cellFactory?.let { f -> f.cellOf(it) }
          ?: c.nodeFactory?.let { f -> asCell(NodeAndChangeListener(f.nodeOf(it))) }
      })
    }

    val allRows = rowsWithoutRemoved + newRows
    val offset = if (newHeaderNodes != null) 1 else 0

    allRows.forEachIndexed { rowIndex, row ->
      row.nodes.forEachIndexed { columnIndex, nodeAndUpdate ->
        if (nodeAndUpdate != null) {
          WeightGridPane.setPosition(nodeAndUpdate.node, columnIndex, rowIndex + offset)
        }
      }
    }

    columns.forEachIndexed { index, column ->
      val header = headerNodes?.get(column)
      if (header != null) grid.children.removeAll(header)
    }
    if (newHeaderNodes != null) {
      columns.forEachIndexed { index, column ->
        val header = newHeaderNodes[column]
        if (header != null) {
          WeightGridPane.setPosition(header, index, 0)
          grid.children.add(header)
        }
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

    columns.forEachIndexed { index, column ->
      val header = footerNodes?.get(column)
      if (header != null) grid.children.removeAll(header)
    }
    if (newFooterNodes != null) {
      columns.forEachIndexed { index, column ->
        val footer = newFooterNodes[column]
        if (footer != null) {
          WeightGridPane.setPosition(footer, index, allRows.size + offset)
          grid.children.add(footer)
        }
      }
    }
    footerNodes = newFooterNodes
  }

  fun interface HeaderFooterNodeFactory<T : Any> {
    fun nodesOf(values: List<T>, columns: List<Column<T>>): Map<Column<T>, Node>
  }

  fun interface ChangeListener<T : Any> {
    fun update(value: T)
  }

  @Deprecated("use CellFactory")
  fun interface NodeFactory<T : Any> {
    fun nodeOf(value: T): Pair<Node, ChangeListener<T>>
  }

  fun interface CellFactory<T: Any, N: Node> {
    fun cellOf(value: T): TableCell<T, out N>
  }

  data class Column<T : Any>(
    val weight: Double = 1.0,
    val nodeFactory: NodeFactory<T>? = null,
    val cellFactory: CellFactory<T, out Node>? = null,
    val horizontalPosition: HPos? = null,
    val verticalPosition: VPos? = null
  )

  private data class NodeAndChangeListener<T : Any>(
    val node: Node,
    val changeListener: ChangeListener<T>
  ) {
    constructor(pair: Pair<Node, ChangeListener<T>>) : this(pair.first, pair.second)
  }

  private data class Row<T : Any, I : Any>(
    val index: I,
    val nodes: List<TableCell<T, out Node>?>
  )

  companion object {
    private fun <T: Any> asCell(src: NodeAndChangeListener<T>): TableCell<T, out Node> {
      return TableCell(src.node) { n, v -> src.changeListener.update(v) }
    }
  }
}