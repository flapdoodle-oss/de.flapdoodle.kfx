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
package de.flapdoodle.kfx.controls.labels

import de.flapdoodle.kfx.converters.ValidatingConverter
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Label
import javafx.util.StringConverter

open class ValidatedLabel<T: Any>(
  val converter: StringConverter<T>
) : Label() {

  private val valueProperty: SimpleObjectProperty<T>

  constructor(converter: ValidatingConverter<T>): this(ValidatingConverter.asStringConverter(converter))

  init {
    this.valueProperty = SimpleObjectProperty<T>(null)
    textProperty().bindBidirectional(valueProperty, converter)
  }

  fun valueProperty(): ObjectProperty<T> = valueProperty
  fun set(v: T?) {
    valueProperty.value = v
  }
}