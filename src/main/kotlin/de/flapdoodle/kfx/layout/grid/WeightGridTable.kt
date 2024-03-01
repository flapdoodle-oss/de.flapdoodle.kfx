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
    value.forEachIndexed { row, t ->
      columns.forEachIndexed { column, c ->
        val node = c.nodeFactory(t)
        WeightGridPane.setPosition(node, column, row, c.horizontalPosition, c.verticalPosition)
        grid.children.add(node)
      }
    }
  }

  class Column<T : Any>(
    val weight: Double = 1.0,
    val nodeFactory: (T) -> Node,
    val horizontalPosition: HPos? = null,
    val verticalPosition: VPos? = null
  )
}