package de.flapdoodle.kfx.i18n

import java.text.MessageFormat
import java.util.Locale
import java.util.MissingResourceException
import java.util.ResourceBundle
import kotlin.reflect.KClass

object I18N {
  fun message(locale: Locale, bundleName: String, key: String, vararg parameter: Any): String? {
    try {
      val bundle = ResourceBundle.getBundle(bundleName, locale)
      val text = bundle.getString(key)
      return MessageFormat.format(text, *parameter)
    } catch (ex: MissingResourceException) {
      IllegalArgumentException("missing resource: locale=$locale, bundleName=$bundleName, key=$key", ex)
        .printStackTrace()

      return null
    }
  }

  fun message(locale: Locale, bundleName: String, type: KClass<*>, key: String, vararg parameter: Any): String? {
    return message(locale, bundleName, "${type.simpleName}.$key", *parameter)
  }

  fun exceptionMessage(locale: Locale, type: KClass<*>, key: String, vararg parameter: Any): String? {
    return message(locale,"exceptions", type, key, *parameter)
  }
}