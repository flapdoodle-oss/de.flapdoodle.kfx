package de.flapdoodle.kfx.converters.impl

import de.flapdoodle.kfx.i18n.I18N
import java.text.ParseException
import java.util.*

class TemporalAccessorParseException(
  private val locale: Locale,
  private val value: String,
  pos: Int
) : ParseException("Text '$value' could not be parsed, unparsed text found at index $pos", pos) {

  override fun getLocalizedMessage(): String {
    return I18N.exceptionMessage(locale, TemporalAccessorParseException::class, "unparseableDate", value)
      ?: super.getLocalizedMessage()
  }
}