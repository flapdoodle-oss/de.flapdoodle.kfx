package de.flapdoodle.kfx.controls.smarttable

import de.flapdoodle.kfx.extensions.cssClassName
import javafx.scene.Node
import javafx.scene.layout.StackPane

abstract class SmartColumn<T : Any, C: Any>(
    header: Node,
    val footer: Node? = null
) : StackPane() {
  init {
    children.add(header)
    cssClassName("smart-header-column")
  }

  abstract fun cell(row: T): SmartCell<T, C>
}