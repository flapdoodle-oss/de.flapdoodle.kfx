package de.flapdoodle.kfx.controls.bettertable

import javafx.scene.Node

data class Column<T: Any, C: Any>(
  val header: () -> Node,
  val cell: (T) -> Cell<T, C>,
  val footer: (() -> Node)? = null
)