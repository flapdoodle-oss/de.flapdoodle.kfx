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

import de.flapdoodle.kfx.converters.ValueOrError
import java.text.NumberFormat
import java.text.ParsePosition
import java.util.Locale

abstract class AbstractNumberConverter(
  protected val locale: Locale,
  protected val format: NumberFormat
) {

  protected fun parse(value: String?): ValueOrError<Number> {
    if (value.isNullOrEmpty()) {
      return ValueOrError.noValue()
    }
    val pos = ParsePosition(0)
    val number = format.parse(value, pos)
      ?: return ValueOrError.Error(NumberParseException(value, 0))
    
    if (pos.index!=value.length) {
      // could not parse everything
      return if (pos.errorIndex == -1) {
        ValueOrError.Error(SomethingLeftException(value, value.substring(pos.index), pos.index))
      } else {
        ValueOrError.Error(NumberParseException(value, pos.errorIndex))
      }
    }

    return ValueOrError.Value(number)
  }

}