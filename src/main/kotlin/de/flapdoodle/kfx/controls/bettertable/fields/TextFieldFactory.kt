package de.flapdoodle.kfx.controls.bettertable.fields

import de.flapdoodle.kfx.controls.bettertable.Cells
import javafx.scene.control.TextField
import javafx.util.StringConverter

class TextFieldFactory<T : Any>(
  private val converter: StringConverter<T>
) : FieldFactory<T> {

  override fun inputFor(
    value: T?,
    commitEdit: (T?) -> Unit,
    cancelEdit: () -> Unit
  ): FieldWrapper<TextField> {
    return TextFieldWrapper(Cells.createTextField(value, converter, commitEdit, cancelEdit))
  }

  class TextFieldWrapper(control: TextField) : FieldWrapper<TextField>(control) {
    override var text: String?
      get() = control.text
      set(value) {
        control.text = value
      }
  }
}