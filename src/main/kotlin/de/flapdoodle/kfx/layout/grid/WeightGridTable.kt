package de.flapdoodle.kfx.layout.grid

import javafx.beans.property.ReadOnlyObjectProperty
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.layout.Region

class WeightGridTable<T : Any>(
  model: ReadOnlyObjectProperty<List<T>>,
  private val columns: List<Column<T>>
) : Region() {
  private val grid = WeightGridPane()

  fun verticalSpace() = grid.verticalSpace
  fun horizontalSpace() = grid.horizontalSpace

  init {
    children.add(grid)
    update(model.value)
    model.addListener { observable, oldValue, newValue ->
      grid.children.clear()
      update(newValue)
    }
  }

  private fun update(value: List<T>) {
    val anyHeader = columns.any { it.headerFactory!=null }
    val offset = if (anyHeader) 1 else 0
    columns.forEachIndexed { column, c ->
      val header = c.headerFactory?.invoke()
      if (header!=null) {
        WeightGridPane.setPosition(header, column, 0, c.horizontalPosition, c.verticalPosition)
        grid.children.add(header)
      }
    }
    value.forEachIndexed { row, t ->
      columns.forEachIndexed { column, c ->
        val node = c.nodeFactory(t)
        WeightGridPane.setPosition(node, column, row + offset, c.horizontalPosition, c.verticalPosition)
        grid.children.add(node)
      }
    }
    columns.forEachIndexed { column, c ->
      val footer = c.footerFactory?.invoke()
      if (footer!=null) {
        WeightGridPane.setPosition(footer, column, value.size + offset, c.horizontalPosition, c.verticalPosition)
        grid.children.add(footer)
      }
    }
  }

  class Column<T : Any>(
    val weight: Double = 1.0,
    val nodeFactory: (T) -> Node,
    val headerFactory: (() -> Node)? = null,
    val footerFactory: (() -> Node)? = null,
    val horizontalPosition: HPos? = null,
    val verticalPosition: VPos? = null
  )
}