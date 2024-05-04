package de.flapdoodle.kfx.controls.fields

import de.flapdoodle.kfx.converters.ValidatingConverter
import javafx.event.EventHandler
import javafx.scene.control.Control
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent

class TextFieldFactory<T : Any>(
  private val converter: ValidatingConverter<T>
) : FieldFactory<T> {
  override fun inputFor(value: T?, commitEdit: (T?) -> Unit, cancelEdit: () -> Unit): FieldWrapper<T, out Control> {
    val textField = ValidatingTextField(converter)
    textField.set(value)

    textField.onKeyReleased = EventHandler { t: KeyEvent ->
      if (t.code == KeyCode.ENTER) {
        t.consume()
        if (!textField.hasError()) {
          commitEdit(textField.get())
        }
      }
      if (t.code == KeyCode.ESCAPE) {
        t.consume()
        cancelEdit()
      }
    }

    return Wrapper(textField)
  }

  class Wrapper<T: Any>(control: ValidatingTextField<T>) : FieldWrapper<T, ValidatingTextField<T>>(control) {
    override var text: String?
      get() = control.text
      set(value) {
        control.text = value
      }

    override var value: T?
      get() = control.get()
      set(value) { control.set(value) }
  }
}