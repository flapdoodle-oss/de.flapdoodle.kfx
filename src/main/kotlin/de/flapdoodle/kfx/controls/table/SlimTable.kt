package de.flapdoodle.kfx.controls.table

import de.flapdoodle.kfx.extensions.bindCss
import javafx.collections.ObservableList
import javafx.scene.control.Control

class SlimTable<T: Any>(
  internal val rows: ObservableList<T>,
) : Control() {
  init {

    bindCss("slim-table")
  }
}