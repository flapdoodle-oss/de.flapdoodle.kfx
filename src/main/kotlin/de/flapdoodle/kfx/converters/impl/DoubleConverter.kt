package de.flapdoodle.kfx.converters.impl

import de.flapdoodle.kfx.converters.ValidatingConverter
import de.flapdoodle.kfx.converters.ValueOrError
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class DoubleConverter(
  locale: Locale,
  maximumFractionDigits: Int = 10,
): AbstractNumberConverter(locale, (NumberFormat.getNumberInstance(locale) as DecimalFormat).also {
  it.maximumFractionDigits = maximumFractionDigits
}),  ValidatingConverter<Double> {

  override fun toString(value: Double): String {
    return format.format(value)
  }

  override fun fromString(value: String): ValueOrError<Double> {
    return parse(value).mapValue {
      it.toDouble()
    }
  }

}