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
import java.text.ParsePosition
import java.time.chrono.Chronology
import java.time.chrono.IsoChronology
import java.time.format.*
import java.time.temporal.TemporalAccessor
import java.util.*

abstract class AbstractTemporalConverter(
  protected val locale: Locale,
  protected val format: DateTimeFormatter
) {

  protected fun parse(value: String): ValueOrError<TemporalAccessor> {
    val pos = ParsePosition(0)
    try {
      val number = format.parse(value, pos)
        ?: return ValueOrError.Error(TemporalAccessorParseException(locale, value, 0))

      if (pos.index != value.length) {
        // could not parse everything
        return if (pos.errorIndex == -1) {
          ValueOrError.Error(SomethingLeftException(locale, value, value.substring(pos.index), pos.index))
        } else {
          ValueOrError.Error(TemporalAccessorParseException(locale, value, pos.errorIndex))
        }
      }

      return ValueOrError.Value(number)
    } catch (ex: DateTimeParseException) {
      return ValueOrError.Error(SomethingLeftException(locale, value, value.substring(ex.errorIndex), ex.errorIndex).apply {
        addSuppressed(ex)
      })
    }
  }

  companion object {
    fun dateTimeFormater(
      dateStyle: FormatStyle,
      timeStyle: FormatStyle?,
      chronology: Chronology = IsoChronology.INSTANCE,
      locale: Locale
    ): DateTimeFormatter {
      val pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(
        dateStyle, timeStyle,
        chronology, locale
      );
      return DateTimeFormatterBuilder().parseLenient()
        .appendPattern(pattern)
        .toFormatter()
        .withChronology(chronology)
        .withDecimalStyle(DecimalStyle.of(locale))
    }
  }
}