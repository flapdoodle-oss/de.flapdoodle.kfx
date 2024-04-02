package de.flapdoodle.kfx.controls.fields

import javafx.scene.control.Control

interface FieldFactory<T: Any> {
  fun inputFor(
    value: T?,
    commitEdit: (T?) -> Unit,
    cancelEdit: () -> Unit
  ): FieldWrapper<T, out Control>
}