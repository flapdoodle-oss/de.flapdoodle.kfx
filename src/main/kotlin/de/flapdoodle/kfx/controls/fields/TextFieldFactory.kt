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

import de.flapdoodle.kfx.controls.bettertable.Cells
import javafx.event.EventHandler
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.util.StringConverter

internal class TextFieldFactory<T : Any>(
  private val converter: StringConverter<T>
) : FieldFactory<T> {

  override fun inputFor(
    value: T?,
    commitEdit: (T?) -> Unit,
    cancelEdit: () -> Unit
  ): FieldWrapper<T, TextField> {
    val textField = TextField()
    textField.text = converter.toString(value)

    textField.onKeyReleased = EventHandler { t: KeyEvent ->
      if (t.code == KeyCode.ENTER) {
        t.consume()
        commitEdit(converter.fromString(textField.text))
      }
      if (t.code == KeyCode.ESCAPE) {
        t.consume()
        cancelEdit()
      }
    }

    return TextFieldWrapper(textField, converter)
  }

  class TextFieldWrapper<T: Any>(control: TextField, val converter: StringConverter<T>) : FieldWrapper<T, TextField>(control) {
    override var text: String?
      get() = control.text
      set(value) {
        control.text = value
      }

    override var value: T?
      get() = converter.fromString(control.text)
      set(value) { control.text = converter.toString(value)}

  }
}