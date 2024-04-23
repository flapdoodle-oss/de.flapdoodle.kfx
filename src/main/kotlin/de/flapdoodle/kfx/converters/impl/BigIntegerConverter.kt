package de.flapdoodle.kfx.converters.impl

import de.flapdoodle.kfx.converters.ValidatingConverter
import de.flapdoodle.kfx.converters.ValueOrError
import java.math.BigInteger
import java.text.NumberFormat
import java.util.*

class BigIntegerConverter(
  locale: Locale,
): AbstractNumberConverter(locale, NumberFormat.getIntegerInstance(locale)), ValidatingConverter<BigInteger> {

  override fun toString(value: BigInteger): String {
    return format.format(value)
  }

  override fun fromString(value: String): ValueOrError<BigInteger> {
    return parse(value).mapValue { BigInteger.valueOf(it.toLong()) }
  }
}