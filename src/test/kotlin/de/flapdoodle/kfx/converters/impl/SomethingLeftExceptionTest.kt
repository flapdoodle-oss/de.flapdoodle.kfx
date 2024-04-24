package de.flapdoodle.kfx.converters.impl

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

class SomethingLeftExceptionTest {
  @Test
  fun localizedMessage() {
    assertThat(SomethingLeftException(Locale.GERMANY, "all","partLeft",123).localizedMessage)
      .isEqualTo("'all' konnte nicht geparst werden, ungeparster Text 'partLeft' an Position 123")
  }
}