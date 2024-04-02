package de.flapdoodle.kfx.controls.bettertable.fields

import de.flapdoodle.kfx.layout.StackLikeRegion
import javafx.scene.control.Control

open abstract class FieldWrapper<C: Control>(
  val control: C
) : StackLikeRegion() {

  abstract var text: String?

  init {
    children.add(control)
  }
}