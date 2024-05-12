/*
 * Copyright (C) 2022
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
      try {
        val text = bundle.getString(key)
        MessageFormat.format(text, *parameter)
      } catch (ex: MissingResourceException) {
        IllegalArgumentException("missing key for locale=$locale, bundleName=$bundleName: $key", ex)
          .printStackTrace()
        null
      }
    } else {
      null
    }
  }
}