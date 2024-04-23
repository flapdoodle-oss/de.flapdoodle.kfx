package de.flapdoodle.kfx.converters.impl

import de.flapdoodle.kfx.converters.ValueOrError
import java.text.ParsePosition
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
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

      if (pos.index!=value.length) {
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

}