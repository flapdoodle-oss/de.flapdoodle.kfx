package de.flapdoodle.kfx.layout.grid

import de.flapdoodle.kfx.collections.Diff
import de.flapdoodle.kfx.extensions.withAnchors
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.layout.AnchorPane

class WeightGridTable<T : Any, I: Any>(
  model: ReadOnlyObjectProperty<List<T>>,
  private val indexOf: (T) -> I,
  private val columns: List<Column<T>>,
  private val headerFactory: ((List<T>) -> List<Node?>)? = null,
  private val footerFactory: ((List<T>) -> List<Node?>)? = null,
) : AnchorPane() {
  private val grid = WeightGridPane()

  private var headerNodes: List<Node?>? = null
  private var rows = emptyList<Row<T, I>>()
  private var footerNodes: List<Node?>? = null

  fun verticalSpace() = grid.verticalSpace
  fun horizontalSpace() = grid.horizontalSpace

  init {
    grid.withAnchors(all = 0.0)
    children.add(grid)

    update(emptyList(), model.value)
    model.addListener { observable, oldValue, newValue ->
//      grid.children.clear()
      update(oldValue, newValue)
    }
  }

  private fun update(old: List<T>, new: List<T>) {
    val newHeaderNodes = headerFactory?.invoke(new)
    val newFooterNodes = footerFactory?.invoke(new)

    val changes = Diff.between(old,new,indexOf)

    val removedIds = changes.removed.map { indexOf(it) }.toSet()
    val modified = changes.modified.map { indexOf(it.second) to it.second }.toMap()
    val addedIds = changes.added.map { indexOf(it) }.toSet()

    val (nodesToRemove, rowsWithoutRemoved) = rows.partition { removedIds.contains(it.index) }

    val newRows = new.filter { addedIds.contains(indexOf(it)) }.map {
      Row(indexOf(it) , columns.map { c -> NodeAndChangeListener(c.nodeFactory.nodeOf(it)) })
    }

    val allRows = rowsWithoutRemoved + newRows
    val offset = if (newHeaderNodes!=null) 1 else 0

    allRows.forEachIndexed { rowIndex, row ->
      row.nodes.forEachIndexed { columnIndex, nodeAndUpdate ->
        WeightGridPane.setPosition(nodeAndUpdate.node, columnIndex, rowIndex + offset)
      }
    }

    headerNodes?.forEach {
      if (it!=null) grid.children.removeAll(it)
    }

    newHeaderNodes?.forEachIndexed { column, header ->
      if (header!=null) {
        WeightGridPane.setPosition(header, column, 0)
        grid.children.add(header)
      }
    }

    headerNodes = newHeaderNodes

    nodesToRemove.forEach { row -> row.nodes.forEach { grid.children.removeAll(it.node)  } }
    rowsWithoutRemoved.forEach { row ->
      val changedValue = modified[row.index]
      if (changedValue!=null) {
        row.nodes.forEach { it.changeListener.update(changedValue) }
      }
    }
    newRows.forEach { row -> row.nodes.forEach { grid.children.add(it.node) } }
    rows = allRows

    footerNodes?.forEach { if (it!=null) grid.children.removeAll(it) }

    newFooterNodes?.forEachIndexed { column, footer ->
      if (footer != null) {
        WeightGridPane.setPosition(footer, column, allRows.size + offset)
        grid.children.add(footer)
      }
    }

    footerNodes = newFooterNodes
  }

  fun interface ChangeListener<T: Any> {
    fun update(value: T)
  }

  fun interface NodeFactory<T: Any> {
    fun nodeOf(value: T): Pair<Node , ChangeListener<T>>
  }

  data class NodeAndChangeListener<T: Any>(
    val node: Node,
    val changeListener: ChangeListener<T>
  ) {
    constructor(pair: Pair<Node, ChangeListener<T>>) : this(pair.first, pair.second)
  }

  data class Row<T: Any, I: Any>(
    val index: I,
    val nodes: List<NodeAndChangeListener<T>>
  )

  data class Column<T : Any>(
    val weight: Double = 1.0,
    val nodeFactory: NodeFactory<T>,
    val horizontalPosition: HPos? = null,
    val verticalPosition: VPos? = null
  )
}