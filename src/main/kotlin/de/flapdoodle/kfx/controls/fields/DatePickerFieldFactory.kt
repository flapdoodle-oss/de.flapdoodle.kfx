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

import de.flapdoodle.kfx.converters.impl.LocalDateConverter
import javafx.scene.control.Control
import java.time.LocalDate
import java.util.*

internal class DatePickerFieldFactory(
  private val locale: Locale
) : FieldFactory<LocalDate> {

  override fun inputFor(value: LocalDate?, commitEdit: (LocalDate?, String?) -> Unit, cancelEdit: () -> Unit): FieldWrapper<LocalDate, out Control> {
    return DatePickerFieldWrapper(ValidatingDatePicker(LocalDateConverter(locale)))
  }

  class DatePickerFieldWrapper(
    control: ValidatingDatePicker,
  ) : FieldWrapper<LocalDate, ValidatingDatePicker>(control) {

    override var text: String?
      get() = control.editor.text
      set(value) {
        control.editor.text = value
      }

    override var value: LocalDate?
      get() = control.value
      set(value) {
        control.value = value
      }

    override var error: String?
      get() = control.errorMessage()
      set(value) {
        control.setErrorMessage(value)
      }
  }
}