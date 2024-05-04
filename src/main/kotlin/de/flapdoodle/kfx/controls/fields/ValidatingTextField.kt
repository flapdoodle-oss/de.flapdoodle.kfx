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
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.TextField
import javafx.scene.layout.Border
import javafx.scene.paint.Color

class ValidatingTextField<T : Any>(
  val converter: ValidatingConverter<T>,
  val onException: (TextField, Exception?) -> Unit = { textfield, ex ->
    if (ex!=null) {
      textfield.tooltip = Tooltips.tooltip(ex.localizedMessage)
      textfield.border = Border.stroke(Color.RED)
    } else {
      textfield.tooltip = null
      textfield.border = null
    }
  }
) : TextField() {

  private val valueProperty = SimpleObjectProperty<T>(null)
  private val lastExceptionProperty = SimpleObjectProperty<Exception>(null)

  fun valueProperty(): ReadOnlyProperty<T> = valueProperty
  fun lastExceptionProperty(): ReadOnlyProperty<Exception> = lastExceptionProperty

  init {
    textProperty().bindBidirectional(valueProperty, ValidatingConverter.asStringConverter(converter, lastExceptionPropertySetter = {
      lastExceptionProperty.value = it
    }))
    lastExceptionProperty().addListener { _, _, exception ->
      onException(this, exception)
    }
  }

  fun set(v: T?) {
    valueProperty.value = v
  }

  fun get(): T? {
    return valueProperty.value
  }

  fun hasError(): Boolean {
    return lastExceptionProperty.value != null
  }
}