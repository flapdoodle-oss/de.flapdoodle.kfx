package de.flapdoodle.kfx.converters.impl

import de.flapdoodle.kfx.i18n.I18N
import java.util.*

class SomethingLeftException(
  private val locale: Locale,
  private val all: String,
  private val partLeft: String,
  private val errorIndex: Int
) : RuntimeException(
  "'$all' could not be parsed, unparsed text '$partLeft' found at index $errorIndex"
//  "could not parse: \"$partLeft\" in \"$all\""
) {

  override fun getLocalizedMessage(): String {
    return I18N.exceptionMessage(locale, SomethingLeftException::class, "couldNotParse", all, partLeft, errorIndex)
      ?: super.getLocalizedMessage()
  }

  fun errorIndex() = errorIndex
}