package de.flapdoodle.kfx.converters.impl

import de.flapdoodle.kfx.converters.ValidatingConverter
import de.flapdoodle.kfx.converters.ValueOrError
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class FloatConverter(
  locale: Locale,
  maximumFractionDigits: Int = 10,
): AbstractNumberConverter(locale, (NumberFormat.getNumberInstance(locale) as DecimalFormat).also {
  it.maximumFractionDigits = maximumFractionDigits
}),  ValidatingConverter<Float> {

  override fun toString(value: Float): String {
    return format.format(value)
  }

  override fun fromString(value: String): ValueOrError<Float> {
    return parse(value).mapValue {
      it.toFloat()
    }
  }

}