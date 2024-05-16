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
import de.flapdoodle.kfx.i18n.ResourceBundles
import java.text.ParseException

class TemporalAccessorParseException(
  private val value: String,
  pos: Int
) : ParseException("Text '$value' could not be parsed, unparsed text found at index $pos", pos) {

  override fun getLocalizedMessage(): String {
    return I18N.exceptionMessage(ResourceBundles.exceptions(), TemporalAccessorParseException::class, "unparseableDate", value)
      ?: super.getLocalizedMessage()
  }

  fun input() = value
}