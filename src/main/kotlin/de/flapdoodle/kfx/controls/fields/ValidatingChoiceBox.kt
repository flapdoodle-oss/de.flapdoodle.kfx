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

import de.flapdoodle.kfx.converters.ReadOnlyStringConverter
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.ChoiceBox
import javafx.util.StringConverter

class ValidatingChoiceBox<T : Any>(
  val values: List<T?>,
  val default: T?,
  val initialConverter: StringConverter<T>,
  val validate: (T?) -> String?
) : ChoiceBox<T>(), ValidatingField<T> {

  constructor(values: List<T?>,
              default: T?,
              initialConverter: (T) -> String,
              validate: (T?) -> String?) : this(values, default, ReadOnlyStringConverter.with(initialConverter), validate)

  private val lastError = SimpleObjectProperty<String>(null)

  init {
    require(default == null || values.contains(default)) { "default value $default is not in selection: $values" }

    items.addAll(values)
    value = default
    converter = initialConverter

    valueProperty().addListener { observable, oldValue, newValue ->
      lastError.value = validate(newValue)
    }
  }

  override fun get(): T? {
    return value
  }

  override fun set(value: T?) {
    this.value = value
  }

  override fun hasError(): Boolean {
    return lastError.value != null
  }

  override fun errorMessage(): String? {
    return lastError.value
  }

  override fun lastErrorProperty(): ReadOnlyProperty<String> {
    return lastError
  }

  override fun valueProperty(): ObjectProperty<T> {
    return super.valueProperty()
  }
}