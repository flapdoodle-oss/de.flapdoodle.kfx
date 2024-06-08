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

import java.util.*
import kotlin.reflect.KClass

object I18N {
  fun resourceBundle(locale: Locale, bundleName: String): ResourceBundleWrapper {
    return ResourceBundleWrapper(locale, bundleName)
  }

  fun exceptionMessage(resourceBundle: ResourceBundleWrapper, type: KClass<*>, key: String, vararg parameter: Any): String? {
    return resourceBundle.message(listOf(
      "${type.simpleName}.$key",
      "${type.qualifiedName}.$key"
    ), *parameter)
  }
}