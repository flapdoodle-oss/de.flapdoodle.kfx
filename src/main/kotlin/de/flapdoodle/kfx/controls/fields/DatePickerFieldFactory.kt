/*
 * Copyright (C) 2022
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.kfx.controls.fields

import de.flapdoodle.kfx.controls.Tooltips
import de.flapdoodle.kfx.converters.impl.LocalDateConverter
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Control
import javafx.scene.control.DatePicker
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Border
import javafx.scene.paint.Color
import java.time.LocalDate
import java.time.chrono.IsoChronology
import java.util.*

internal class DatePickerFieldFactory(
  private val locale: Locale
) : FieldFactory<LocalDate> {

  override fun inputFor(value: LocalDate?, commitEdit: (LocalDate?, String?) -> Unit, cancelEdit: () -> Unit): FieldWrapper<LocalDate, out Control> {
    val errorMessage = SimpleObjectProperty<String>(null)

    val control = DatePicker().apply {
      this.chronology = IsoChronology.INSTANCE
      this.converter = LocalDateConverter(locale, chronology).asStringConverter {
        errorMessage.value = it?.localizedMessage
      }
      this.value = value

      addEventHandler(KeyEvent.KEY_RELEASED) { t ->
        if (t.code == KeyCode.ESCAPE) {
          t.consume()
          cancelEdit()
        }
      }
      editor.addEventHandler(KeyEvent.KEY_RELEASED) { t ->
        if (t.code == KeyCode.ENTER) {
          t.consume()
          if (errorMessage.value == null) {
            commitEdit(this.value, null)
          }
        }
      }
    }

    errorMessage.addListener { _, _, error ->
      if (error != null) {
        control.tooltip = Tooltips.tooltip(error)
        control.border = Border.stroke(Color.RED)
      } else {
        control.tooltip = null
        control.border = null
      }
    }

    return DatePickerFieldWrapper(control, errorMessage)
  }

  class DatePickerFieldWrapper(
    control: DatePicker,
    private val errorMessage: SimpleObjectProperty<String>
  ) : FieldWrapper<LocalDate, DatePicker>(control) {

    override var text: String?
      get() = control.editor.text
      set(value) { control.editor.text = value }

    override var value: LocalDate?
      get() = control.value
      set(value) { control.value = value }

    override var error: String?
      get() = errorMessage.value
      set(value) { errorMessage.value = value }
  }
}