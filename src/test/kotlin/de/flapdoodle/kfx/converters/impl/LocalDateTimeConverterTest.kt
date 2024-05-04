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

import de.flapdoodle.kfx.converters.ValueOrError
import org.assertj.core.api.Assertions
import org.assertj.core.api.InstanceOfAssertFactories
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.util.*

class LocalDateTimeConverterTest {
  private val testee = LocalDateTimeConverter(Locale.GERMANY)

  @Test
  fun validDate() {
    val value = LocalDateTime.of(2023, Month.MAY, 13, 12, 55, 30, 0)

    val asString = testee.toString(value)
    Assertions.assertThat(asString).isEqualTo("13.05.2023, 12:55:30")

    val readBack = testee.fromString(asString)
    Assertions.assertThat(readBack).isEqualTo(ValueOrError.Value(value))
  }

  @Test
  fun invalidDate() {
    val asString = "13.05.2023a"

    val readBack = testee.fromString(asString)
    Assertions.assertThat((readBack as ValueOrError.Error).exception)
      .hasMessage("'13.05.2023a' could not be parsed, unparsed text 'a' found at index 10")
      .asInstanceOf(InstanceOfAssertFactories.type(SomethingLeftException::class.java))
      .extracting(SomethingLeftException::errorIndex).isEqualTo(10)
  }

}