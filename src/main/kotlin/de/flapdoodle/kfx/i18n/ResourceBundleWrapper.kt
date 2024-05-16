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

import de.flapdoodle.types.Either
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
    return property(key)?.let { MessageFormat.format(it, *parameter) }
  }

  fun message(keys: List<String>, vararg parameter: Any): String? {
    var keyAndExceptions = emptyList<Pair<String, MissingResourceException>>()

    keys.forEach { key ->
      val property = propertyOrException(key)
      if (property != null) {
        if (property.isLeft) {
          return MessageFormat.format(property.left(), *parameter)
        } else {
          keyAndExceptions = keyAndExceptions + (key to property.right())
        }
      }
    }
    if (keyAndExceptions.isNotEmpty()) {
      val keys = keyAndExceptions.map { it.first }.joinToString(separator = ",")
      val exceptions = keyAndExceptions.map { it.second }
      IllegalArgumentException("missing key for locale=$locale, bundleName=$bundleName: $keys", exceptions.first())
        .apply { exceptions.subList(1, exceptions.size).forEach { ex -> addSuppressed(ex)} }
        .printStackTrace()
    }
    return null
  }

  private fun property(key: String): String? {
    val result = propertyOrException(key)
    return if (result!=null) {
      if (result.isLeft) result.left()
      else {
        IllegalArgumentException("missing key for locale=$locale, bundleName=$bundleName: $key", result.right())
          .printStackTrace()
        null
      }
    } else {
      null
    }

//    return if (bundle!=null) {
//      try {
//        val text = bundle.getString(key)
//        return text
//      } catch (ex: MissingResourceException) {
//        IllegalArgumentException("missing key for locale=$locale, bundleName=$bundleName: $key", ex)
//          .printStackTrace()
//        null
//      }
//    } else {
//      null
//    }
  }

  private fun propertyOrException(key: String): Either<String, MissingResourceException>? {
    return if (bundle!=null) {
      try {
        val text = bundle.getString(key)
        return Either.left(text)
      } catch (ex: MissingResourceException) {
        return Either.right(ex)
      }
    } else {
      null
    }
  }
}