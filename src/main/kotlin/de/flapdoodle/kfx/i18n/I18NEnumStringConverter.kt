package de.flapdoodle.kfx.i18n

import javafx.util.StringConverter
import java.util.*
import kotlin.reflect.KClass

class I18NEnumStringConverter<T : Enum<T>>(
  private val locale: Locale,
  private val bundleName: String,
  private val enumType: KClass<T>
) : StringConverter<T>() {
  val prefix = requireNotNull(enumType.qualifiedName) { "qualified name is null for: $enumType" }

  override fun toString(value: T?): String {
    return I18N.message(locale, bundleName, "${prefix}_${value?.name ?: "NULL"}") ?: "$value"
  }

  override fun fromString(value: String): T {
    throw IllegalArgumentException("not supported: $value")
  }
}