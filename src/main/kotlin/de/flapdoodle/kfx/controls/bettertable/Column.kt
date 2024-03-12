package de.flapdoodle.kfx.controls.bettertable

import javafx.scene.Node

open class Column<T: Any, C: Any>(
  internal val header: () -> Node,
  internal val cell: (T) -> Cell<T, C>,
  internal val footer: (() -> Node)? = null
) {
}