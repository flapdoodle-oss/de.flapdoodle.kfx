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

import javafx.util.StringConverter
import kotlin.reflect.KClass

class I18NEnumStringConverter<T : Enum<T>>(
  private val resourceBundle: ResourceBundleWrapper,
  private val enumType: KClass<T>
) : StringConverter<T>() {
  private val prefix = listOf(
    requireNotNull(enumType.qualifiedName) { "qualified name is null for: $enumType" },
    requireNotNull(enumType.simpleName) { "simpleName name is null for: $enumType" }
  )

  override fun toString(value: T?): String {
    return resourceBundle.message(prefix.map { "${it}_${value?.name ?: "NULL"}" }) ?: "$value"
  }

  override fun fromString(value: String): T {
    throw IllegalArgumentException("not supported: $value")
  }
}