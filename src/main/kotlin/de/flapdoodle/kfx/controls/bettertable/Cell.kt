package de.flapdoodle.kfx.controls.bettertable

import javafx.scene.control.Control
import javafx.scene.text.TextAlignment
import javafx.util.StringConverter

class Cell<T: Any, C: Any>(
  val value: C?,
  val converter: StringConverter<C>,
  val editable: Boolean = false,
  val textAlignment: TextAlignment = TextAlignment.LEFT
): Control() {
  
}