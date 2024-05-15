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
package de.flapdoodle.kfx.converters

import javafx.util.StringConverter

interface ValidatingConverter<T: Any> {
  fun toString(value: T): String
  fun fromString(value: String): ValueOrError<T>

  fun and(check: (ValueOrError<T>) -> ValueOrError<T>): ValidatingConverter<T> {
    val delegate = this
    return object : ValidatingConverter<T> {
      override fun toString(value: T): String {
        return delegate.toString(value)
      }

      override fun fromString(value: String): ValueOrError<T> {
        return check(delegate.fromString(value))
      }
    }
  }

  fun asStringConverter(lastExceptionPropertySetter: (Exception?) -> Unit): StringConverter<T> {
    return asStringConverter(this, lastExceptionPropertySetter)
  }

  companion object {
    fun <T: Any> asStringConverter(
      converter: ValidatingConverter<T>,
      lastExceptionPropertySetter: (Exception?) -> Unit = {}
    ): StringConverter<T> {
      return object : StringConverter<T>() {
        override fun toString(value: T?): String? {
          return value?.let {
            lastExceptionPropertySetter(null)
            converter.toString(it)
          }
        }

        override fun fromString(value: String?): T? {
          return if (value != null && value.trim().isNotEmpty()) {
            when (val v = converter.fromString(value)) {
              is ValueOrError.Value -> {
                lastExceptionPropertySetter(null)
                v.value
              }

              is ValueOrError.Error -> {
                lastExceptionPropertySetter(v.exception)
                null
              }
            }
          } else null
        }
      }
    }

  }
}