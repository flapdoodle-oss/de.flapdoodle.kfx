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
package de.flapdoodle.kfx.converters.impl

import de.flapdoodle.kfx.converters.ValidatingConverter
import de.flapdoodle.kfx.converters.ValueOrError
import java.text.NumberFormat
import java.text.ParseException
import java.text.ParsePosition
import java.util.*

class IntConverter(
  locale: Locale,
): AbstractNumberConverter(locale, NumberFormat.getIntegerInstance(locale)), ValidatingConverter<Int> {

  override fun toString(value: Int): String {
    return format.format(value)
  }

  override fun fromString(value: String): ValueOrError<Int> {
    return parse(value).mapValue { Math.toIntExact(it.toLong()) }
  }
}