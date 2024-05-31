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
import de.flapdoodle.kfx.converters.ValidatingConverter
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.collections.ListChangeListener
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.TextField
import javafx.scene.control.skin.TextFieldSkin
import javafx.scene.layout.Border
import javafx.scene.paint.Color
import javafx.scene.text.Text

class ValidatingTextField<T : Any>(
  val converter: ValidatingConverter<T>,
  val default: T? = null,
  val mapException: (Exception) -> String = { it.localizedMessage },
  val onError: (TextField, String?) -> Unit = { textfield, error ->
    if (error != null) {
      textfield.tooltip = Tooltips.tooltip(error)
      textfield.border = Border.stroke(Color.RED)
    } else {
      textfield.tooltip = null
      textfield.border = null
    }
  }
) : TextField(), ValidatingField<T> {

  private val valueProperty = SimpleObjectProperty<T>(null)
  private val lastError = SimpleObjectProperty<String>(null)

  override fun valueProperty(): ObjectProperty<T> = valueProperty
  override fun lastErrorProperty(): ReadOnlyProperty<String> = lastError

  init {
    textProperty().bindBidirectional(valueProperty, ValidatingConverter.asStringConverter(converter, lastExceptionPropertySetter = {
      lastError.value = if (it!=null) mapException(it) else null
    }))
    lastError.addListener { _, _, error ->
      onError(this, error)
    }
  }

  override fun set(v: T?) {
    valueProperty.value = v
  }

  override fun get(): T? {
    return valueProperty.value
  }

  override fun hasError(): Boolean {
    return lastError.value != null
  }

  fun setErrorMessage(message: String?) {
    lastError.value = message
  }

  override fun errorMessage() = lastError.value
}
