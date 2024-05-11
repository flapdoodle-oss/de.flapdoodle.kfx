package de.flapdoodle.kfx.i18n

import javafx.util.StringConverter
import java.util.*
import kotlin.reflect.KClass

class I18NTypeStringConverter<T: KClass<out Any>>(
  private val locale: Locale,
  private val bundleName: String
) : StringConverter<T>() {
  override fun toString(value: T): String {
    return I18N.message(locale,bundleName,"${value.qualifiedName}") ?: "$value"
  }

  override fun fromString(value: String): T {
    throw IllegalArgumentException("not supported: $value")
  }
}