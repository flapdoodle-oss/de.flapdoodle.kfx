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
package de.flapdoodle.kfx.converters.impl

import de.flapdoodle.kfx.i18n.I18N
import java.util.*

class SomethingLeftException(
  private val locale: Locale,
  private val all: String,
  private val partLeft: String,
  private val errorIndex: Int
) : RuntimeException(
  "'$all' could not be parsed, unparsed text '$partLeft' found at index $errorIndex"
//  "could not parse: \"$partLeft\" in \"$all\""
) {

  override fun getLocalizedMessage(): String {
    return I18N.exceptionMessage(locale, SomethingLeftException::class, "couldNotParse", all, partLeft, errorIndex)
      ?: super.getLocalizedMessage()
  }

  fun input() = all
  fun errorIndex() = errorIndex
}