package de.flapdoodle.kfx.converters.impl

import de.flapdoodle.kfx.converters.ValidatingConverter
import de.flapdoodle.kfx.converters.ValueOrError
import java.time.LocalDate
import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DecimalStyle
import java.time.format.FormatStyle
import java.util.*

class LocalDateConverter(
  locale: Locale
) : AbstractTemporalConverter(locale, DateTimeFormatterBuilder().parseLenient()
  .appendPattern(DateTimeFormatterBuilder
    .getLocalizedDateTimePattern(FormatStyle.MEDIUM, null, IsoChronology.INSTANCE, locale))
  .toFormatter(locale)
  .withDecimalStyle(DecimalStyle.of(locale))), ValidatingConverter<LocalDate> {

  override fun toString(value: LocalDate): String {
    return value.format(format)
  }

  override fun fromString(value: String): ValueOrError<LocalDate> {
    return parse(value).mapValue { it.query(LocalDate::from) }
  }
}