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
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.InstanceOfAssertFactories
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.text.ParseException
import java.util.*

class BigDecimalConverterTest {
  private val testee = BigDecimalConverter(Locale.GERMANY)

  @Test
  fun validNumber() {
    val value = BigDecimal.valueOf(123.987)

    val asString = testee.toString(value)
    assertThat(asString).isEqualTo("123,987")

    val readBack = testee.fromString(asString)
    assertThat(readBack).isEqualTo(ValueOrError.Value(value))
  }

  @Test
  fun veryBigNumber() {
    val value = BigDecimal.valueOf(123.987*100000000).plus(BigDecimal.valueOf(0.12345678901234))

    val asString = testee.toString(value)
    assertThat(asString).isEqualTo("12.398.700.000,12345678901234")

    val readBack = testee.fromString(asString)
    assertThat(readBack).isEqualTo(ValueOrError.Value(value))
  }

  @Test
  fun startsWithANumber() {
    val readBack = testee.fromString("123a")
    assertThat(readBack)
      .isInstanceOf(ValueOrError.Error::class.java)

    assertThat((readBack as ValueOrError.Error).exception)
      .hasMessage("'123a' could not be parsed, unparsed text 'a' found at index 3")

  }

  @Test
  fun endsWithANumber() {
    val readBack = testee.fromString("a123")

    assertThat(readBack)
      .isInstanceOf(ValueOrError.Error::class.java)

    assertThat((readBack as ValueOrError.Error).exception)
      .hasMessage("Unparseable number: \"a123\"")
      .asInstanceOf(InstanceOfAssertFactories.type(ParseException::class.java))
      .extracting(ParseException::getErrorOffset).isEqualTo(0)
  }

  @Test
  fun looksValidButIsNot() {
    val readBack = testee.fromString("--1243.12,1")
    assertThat(readBack)
      .isInstanceOf(ValueOrError.Error::class.java)

    assertThat((readBack as ValueOrError.Error).exception)
      .hasMessage("Unparseable number: \"--1243.12,1\"")
      .asInstanceOf(InstanceOfAssertFactories.type(ParseException::class.java))
      .extracting(ParseException::getErrorOffset).isEqualTo(0)
  }
}