package de.flapdoodle.kfx.converters.impl

import de.flapdoodle.kfx.converters.ValueOrError
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.InstanceOfAssertFactories
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.text.ParseException
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeParseException
import java.util.*

class LocalDateConverterTest {
  private val testee = LocalDateConverter(Locale.GERMANY)

  @Test
  fun validDate() {
    val value = LocalDate.of(2023, Month.MAY, 13)

    val asString = testee.toString(value)
    assertThat(asString).isEqualTo("13.05.2023")

    val readBack = testee.fromString(asString)
    assertThat(readBack).isEqualTo(ValueOrError.Value(value))
  }

  @Test
  fun invalidDate() {
    val asString = "13.05.2023a"

    val readBack = testee.fromString(asString)
    assertThat((readBack as ValueOrError.Error).exception)
      .hasMessage("'13.05.2023a' could not be parsed, unparsed text 'a' found at index 10")
      .asInstanceOf(InstanceOfAssertFactories.type(SomethingLeftException::class.java))
      .extracting(SomethingLeftException::errorIndex).isEqualTo(10)
  }
}