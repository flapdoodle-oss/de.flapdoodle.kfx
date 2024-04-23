package de.flapdoodle.kfx.converters.impl

import de.flapdoodle.kfx.converters.ValueOrError
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.InstanceOfAssertFactories
import org.junit.jupiter.api.Test
import java.text.ParseException
import java.util.*

class DoubleConverterTest {
  private val testee = DoubleConverter(Locale.GERMANY)

  @Test
  fun validNumber() {
    val value = 123.987

    val asString = testee.toString(value)
    assertThat(asString).isEqualTo("123,987")

    val readBack = testee.fromString(asString)
    assertThat(readBack).isEqualTo(ValueOrError.Value(value))
  }

  @Test
  fun veryBigNumber() {
    val value = 123.987*100000000+0.123456789

    val asString = testee.toString(value)
    assertThat(asString).isEqualTo("12.398.700.000,123457")

    val readBack = testee.fromString(asString)
    assertThat(readBack).isEqualTo(ValueOrError.Value(12398700000.123457))
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