package de.flapdoodle.kfx.i18n

import java.text.MessageFormat
import java.util.*

class ResourceBundleWrapper(
  val locale: Locale,
  val bundleName: String
) {

  private val bundle: ResourceBundle? = try {
    ResourceBundle.getBundle(bundleName, locale)
  } catch (ex: MissingResourceException) {
    IllegalArgumentException("missing resource: locale=$locale, bundleName=$bundleName", ex)
      .printStackTrace()
    null
  }

  fun message(key: String, vararg parameter: Any): String? {
    return if (bundle!=null) {
      val text = bundle.getString(key)
      MessageFormat.format(text, *parameter)
    } else {
      null
    }
  }
}