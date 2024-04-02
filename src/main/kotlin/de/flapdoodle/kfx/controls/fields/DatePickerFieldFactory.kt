package de.flapdoodle.kfx.controls.fields

import javafx.event.EventHandler
import javafx.scene.control.Control
import javafx.scene.control.DatePicker
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import java.time.LocalDate

internal class DatePickerFieldFactory : FieldFactory<LocalDate> {

  override fun inputFor(value: LocalDate?, commitEdit: (LocalDate?) -> Unit, cancelEdit: () -> Unit): FieldWrapper<LocalDate, out Control> {
    return DatePickerFieldWrapper(DatePicker().apply {
//      editor.text = converter.toString(value)
      this.value = value
//      onAction = EventHandler {
//        it.consume()
//        commitEdit(this.value)
//      }
      addEventHandler(KeyEvent.KEY_RELEASED) { t ->
        if (t.code == KeyCode.ESCAPE) {
          t.consume()
          cancelEdit()
        }
      }
      editor.addEventHandler(KeyEvent.KEY_RELEASED) { t ->
        if (t.code == KeyCode.ENTER) {
          t.consume()
          commitEdit(this.value)
        }
      }
    })
  }

  class DatePickerFieldWrapper(
    control: DatePicker
  ) : FieldWrapper<LocalDate, DatePicker>(control) {

    override var text: String?
      get() = control.editor.text
      set(value) { control.editor.text = value }

    override var value: LocalDate?
      get() = control.value
      set(value) { control.value = value }
  }
}