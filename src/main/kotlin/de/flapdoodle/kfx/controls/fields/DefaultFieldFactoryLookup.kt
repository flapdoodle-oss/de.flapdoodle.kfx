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

import de.flapdoodle.kfx.converters.Converters
import de.flapdoodle.reflection.TypeInfo
import java.time.LocalDate
import java.util.*

class DefaultFieldFactoryLookup(
  private val locale: Locale = Locale.getDefault()
) : FieldFactoryLookup {

  private val localDateType = TypeInfo.of(LocalDate::class.java)

  override fun <T : Any> fieldFactory(type: TypeInfo<T>): FieldFactory<T> {
    if (localDateType == type) {
      return DatePickerFieldFactory(locale) as FieldFactory<T>
    }
    return TextFieldFactory(Converters.validatingFor(type, locale))
  }
}