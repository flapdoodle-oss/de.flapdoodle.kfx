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