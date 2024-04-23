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