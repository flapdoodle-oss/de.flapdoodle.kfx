package de.flapdoodle.kfx.controls.bettertable.fields

import javafx.event.EventHandler
import javafx.scene.control.Control
import javafx.scene.control.DatePicker
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import java.time.LocalDate

class DatePickerFieldFactory : FieldFactory<LocalDate> {

  override fun inputFor(value: LocalDate?, commitEdit: (LocalDate?) -> Unit, cancelEdit: () -> Unit): FieldWrapper<out Control> {
    return DatePickerFieldWrapper(DatePicker().apply {
//      editor.text = converter.toString(value)
      this.value = value
      editor.onKeyReleased = EventHandler { t: KeyEvent ->
        if (t.code == KeyCode.ENTER) {
          t.consume()
          commitEdit(this.value)
        }
        if (t.code == KeyCode.ESCAPE) {
          t.consume()
          cancelEdit()
        }
      }
    })
  }

  class DatePickerFieldWrapper(
    control: DatePicker
  ) : FieldWrapper<DatePicker>(control) {

    override var text: String?
      get() = control.editor.text
      set(value) { control.editor.text = value }
  }
}