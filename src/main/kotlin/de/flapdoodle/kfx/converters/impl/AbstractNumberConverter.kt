package de.flapdoodle.kfx.converters.impl

import de.flapdoodle.kfx.converters.ValueOrError
import java.text.NumberFormat
import java.text.ParsePosition
import java.util.Locale

abstract class AbstractNumberConverter(
  protected val locale: Locale,
  protected val format: NumberFormat
) {

  protected fun parse(value: String): ValueOrError<Number> {
    val pos = ParsePosition(0)
    val number = format.parse(value, pos)
      ?: return ValueOrError.Error(NumberParseException(locale, value, 0))
    
    if (pos.index!=value.length) {
      // could not parse everything
      return if (pos.errorIndex == -1) {
        ValueOrError.Error(SomethingLeftException(locale, value, value.substring(pos.index), pos.index))
      } else {
        ValueOrError.Error(NumberParseException(locale, value, pos.errorIndex))
      }
    }

    return ValueOrError.Value(number)
  }

}