package de.flapdoodle.kfx.controls.table

import javafx.scene.Node

data class Column<T: Any, C: Any>(
  val header: () -> Node,
//  val cell: ()
  val footer: () -> Node?
) {
}