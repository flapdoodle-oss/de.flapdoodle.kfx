package de.flapdoodle.kfx.layout.grid

import de.flapdoodle.kfx.extensions.withAnchors
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane

class WeightGridTable<T : Any>(
  model: ReadOnlyObjectProperty<List<T>>,
  private val columns: List<Column<T>>,
  private val headerFactory: ((List<T>) -> List<Node?>)? = null,
  private val footerFactory: ((List<T>) -> List<Node?>)? = null,
) : AnchorPane() {
  private val grid = WeightGridPane()

  fun verticalSpace() = grid.verticalSpace
  fun horizontalSpace() = grid.horizontalSpace

  init {
    grid.withAnchors(all = 0.0)
    children.add(grid)
    update(model.value)
    model.addListener { observable, oldValue, newValue ->
      grid.children.clear()
      update(newValue)
    }
  }

  private fun update(value: List<T>) {
    val headerNodes = headerFactory?.invoke(value)
    val footerNodes = footerFactory?.invoke(value)

    val offset = if (headerNodes!=null) 1 else 0
    headerNodes?.forEachIndexed { column, header ->
      if (header!=null) {
        WeightGridPane.setPosition(header, column, 0)
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

    footerNodes?.forEachIndexed { column, footer ->
      if (footer != null) {
        WeightGridPane.setPosition(footer, column, value.size + offset)
        grid.children.add(footer)
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