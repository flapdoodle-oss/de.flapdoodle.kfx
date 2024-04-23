package de.flapdoodle.kfx.converters.impl

import de.flapdoodle.kfx.converters.ValidatingConverter
import de.flapdoodle.kfx.converters.ValueOrError
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class BigDecimalConverter(
  locale: Locale,
  maximumFractionDigits: Int = 20,
): AbstractNumberConverter(
  locale,
  (NumberFormat.getNumberInstance(locale) as DecimalFormat).also {
    it.maximumFractionDigits = maximumFractionDigits
    it.isParseBigDecimal = true
  }
), ValidatingConverter<BigDecimal> {

  override fun toString(value: BigDecimal): String {
    return format.format(value)
  }

  override fun fromString(value: String): ValueOrError<BigDecimal> {
    return parse(value).mapValue { it as BigDecimal }
  }
}