package de.flapdoodle.kfx.controls.table

import javafx.beans.value.ObservableValue
import javafx.scene.Node

data class Column<T: Any, C: Any>(
  val header: () -> Node,
  val cell: (T) -> SlimCell<T, C>,
  val footer: (() -> Node)? = null
) {
}