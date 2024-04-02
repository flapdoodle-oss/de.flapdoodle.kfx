package de.flapdoodle.kfx.controls.fields

import de.flapdoodle.kfx.layout.StackLikeRegion
import javafx.scene.control.Control

open abstract class FieldWrapper<T: Any, C: Control>(
  val control: C
) : StackLikeRegion() {

  abstract var text: String?
  abstract var value: T?

  init {
    children.add(control)
  }
}