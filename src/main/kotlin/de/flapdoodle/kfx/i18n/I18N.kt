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