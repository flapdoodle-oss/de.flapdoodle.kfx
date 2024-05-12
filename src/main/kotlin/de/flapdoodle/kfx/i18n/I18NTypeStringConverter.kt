package de.flapdoodle.kfx.i18n

import javafx.util.StringConverter
import java.util.*
import kotlin.reflect.KClass

class I18NTypeStringConverter<T : KClass<out Any>>(
  private val locale: Locale,
  private val bundleName: String
) : StringConverter<T>() {
  private val resourceBundle = I18N.resourceBundle(locale, bundleName)

  override fun toString(value: T?): String {
    return resourceBundle.message(value?.qualifiedName ?: "NULL") ?: "$value"
  }

  override fun fromString(value: String): T {
    throw IllegalArgumentException("not supported: $value")
  }
}