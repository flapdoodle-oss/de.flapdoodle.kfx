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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.chrono.Chronology
import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DecimalStyle
import java.time.format.FormatStyle
import java.util.*

class LocalDateTimeConverter(
  locale: Locale,
  chronology: Chronology = IsoChronology.INSTANCE
) : AbstractTemporalConverter(
  locale,
  dateTimeFormater(FormatStyle.MEDIUM, FormatStyle.MEDIUM, chronology, locale)
), ValidatingConverter<LocalDateTime> {

  override fun toString(value: LocalDateTime): String {
    return value.format(format)
  }

  override fun fromString(value: String): ValueOrError<LocalDateTime> {
    return parse(value).mapValue { it.query(LocalDateTime::from) }
  }
}