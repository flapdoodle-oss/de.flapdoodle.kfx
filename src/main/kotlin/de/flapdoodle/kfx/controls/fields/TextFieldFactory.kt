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

import de.flapdoodle.kfx.converters.ValidatingConverter
import javafx.event.EventHandler
import javafx.scene.control.Control
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent

class TextFieldFactory<T : Any>(
  private val converter: ValidatingConverter<T>
) : FieldFactory<T> {
  override fun inputFor(value: T?, commitEdit: (T?, String?) -> Unit, cancelEdit: () -> Unit): FieldWrapper<T, out Control> {
    val textField = ValidatingTextField(converter)
    textField.set(value)

    textField.onKeyReleased = EventHandler { t: KeyEvent ->
      if (t.code == KeyCode.ENTER) {
        t.consume()
        if (!textField.hasError()) {
          commitEdit(textField.get(), textField.errorMessage())
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

    override var error: String?
      get() = control.errorMessage()
      set(value) { control.setErrorMessage(value) }
  }
}