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
package de.flapdoodle.kfx.controls.textfields

import de.flapdoodle.kfx.converters.Converters
import de.flapdoodle.kfx.converters.ValidatingConverter
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Label
import java.util.Locale
import kotlin.reflect.KClass

class ValidatedLabel<T: Any>(
  val converter: ValidatingConverter<T>,
) : Label() {

  private val valueProperty = SimpleObjectProperty<T>(null)

  init {
    textProperty().bindBidirectional(valueProperty, ValidatingConverter.asStringConverter(converter))
  }

  fun valueProperty(): ObjectProperty<T> = valueProperty

  fun set(v: T?) {
    valueProperty.value = v
  }
}