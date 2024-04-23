package de.flapdoodle.kfx.converters.impl

import de.flapdoodle.kfx.converters.ValidatingConverter
import de.flapdoodle.kfx.converters.ValueOrError
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DecimalStyle
import java.time.format.FormatStyle
import java.util.*

class LocalDateTimeConverter(
  locale: Locale
) : AbstractTemporalConverter(locale, DateTimeFormatterBuilder().parseLenient()
  .appendPattern(DateTimeFormatterBuilder
    .getLocalizedDateTimePattern(FormatStyle.MEDIUM, FormatStyle.MEDIUM, IsoChronology.INSTANCE, locale))
  .toFormatter(locale)
  .withDecimalStyle(DecimalStyle.of(locale))), ValidatingConverter<LocalDateTime> {

  override fun toString(value: LocalDateTime): String {
    return value.format(format)
  }

  override fun fromString(value: String): ValueOrError<LocalDateTime> {
    return parse(value).mapValue { it.query(LocalDateTime::from) }
  }
}