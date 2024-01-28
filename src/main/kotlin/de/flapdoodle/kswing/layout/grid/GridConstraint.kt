package de.flapdoodle.kswing.layout.grid

import de.flapdoodle.kfx.layout.grid.GridMap
import javafx.geometry.HPos
import javafx.geometry.VPos

data class GridConstraint(
  val column: Int,
  val row: Int,
  val horizontalPosition: HPos? = null,
  val verticalPosition: VPos? = null
) {
  fun asPos(): GridMap.Pos {
    return GridMap.Pos(column,row)
  }
}
