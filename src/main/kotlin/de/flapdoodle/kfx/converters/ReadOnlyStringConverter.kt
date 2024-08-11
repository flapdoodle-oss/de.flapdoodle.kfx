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

abstract class ReadOnlyStringConverter<T> : StringConverter<T>() {
  final override fun fromString(value: String): T {
    throw IllegalArgumentException("not supported (value = $value)");
  }

  companion object {
    fun <T> with(converter: (T?) -> String): ReadOnlyStringConverter<T> {
      return object : ReadOnlyStringConverter<T>() {
        override fun toString(value: T?): String {
          return converter(value)
        }
      }
    }
  }
}