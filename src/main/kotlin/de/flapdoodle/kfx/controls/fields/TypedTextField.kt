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

import de.flapdoodle.kfx.converters.CatchingStringConverter
import de.flapdoodle.kfx.converters.Converters
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.TextField
import kotlin.reflect.KClass

@Deprecated("see ValidatingTextField")
class TypedTextField<T: Any>(
  val type: KClass<T>
) : TextField() {

  private val valueProperty = SimpleObjectProperty<T>(null)
  private val lastExceptionProperty = SimpleObjectProperty<Exception>(null)
  private val converter = CatchingStringConverter(Converters.converterFor(type), onFromString = {
    lastExceptionProperty.value = it
  })

  fun valueProperty(): ReadOnlyProperty<T> = valueProperty
  fun lastExceptionProperty(): ReadOnlyProperty<Exception> = lastExceptionProperty

  init {
    textProperty().bindBidirectional(valueProperty, converter)
  }

  fun set(v: T?) {
    valueProperty.value = v
  }

  fun get(): T? {
    return valueProperty.value
  }
}