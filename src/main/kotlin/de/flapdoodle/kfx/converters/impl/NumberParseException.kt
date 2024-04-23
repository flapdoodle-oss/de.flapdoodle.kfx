package de.flapdoodle.kfx.converters.impl

import de.flapdoodle.kfx.i18n.I18N
import java.text.ParseException
import java.util.*

class NumberParseException(
  private val locale: Locale,
  private val value: String,
  pos: Int
) : ParseException("Unparseable number: \"$value\"", pos) {

  override fun getLocalizedMessage(): String {
    return I18N.exceptionMessage(locale, NumberParseException::class, "unparseableNumber", value)
      ?: super.getLocalizedMessage()
  }
}