package de.flapdoodle.kfx.converters.impl

import de.flapdoodle.kfx.converters.ValidatingConverter
import de.flapdoodle.kfx.converters.ValueOrError
import java.text.NumberFormat
import java.text.ParseException
import java.text.ParsePosition
import java.util.*

class LongConverter(
  locale: Locale,
): AbstractNumberConverter(locale, NumberFormat.getIntegerInstance(locale)), ValidatingConverter<Long> {

  override fun toString(value: Long): String {
    return format.format(value)
  }

  override fun fromString(value: String): ValueOrError<Long> {
    return parse(value).mapValue { it.toLong() }
  }
}