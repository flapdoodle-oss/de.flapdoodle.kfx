package de.flapdoodle.kfx.bindings

import javafx.beans.property.SimpleObjectProperty
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ObjectBindingsTest {

  @Test
  fun defaultIfNullRecovers() {
    val source = SimpleObjectProperty("source")
    val fallback = SimpleObjectProperty("fallback")
    val testee = ObjectBindings.defaultIfNull(source, fallback)

    assertThat(testee.value).isEqualTo("source")

    source.value = null

    assertThat(testee.value).isEqualTo("fallback")

    source.value = "restored"

    assertThat(testee.value).isEqualTo("restored")
  }
}