package de.flapdoodle.kfx.layout.grid

import javafx.scene.Node

data class TableCell<T: Any, N: Node>(
  val node: N,
  val update: (T) -> Unit = { }
)