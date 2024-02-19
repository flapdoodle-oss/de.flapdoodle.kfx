package de.flapdoodle.kfx.controls.grapheditor.slots

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.UUID

class SlotTest {

  @Test
  fun hashedColorMustAlwaysGetOne() {
    (0..1000).forEach {
      val name = UUID.randomUUID().toString()
      assertThat(Slot.hashedColor(name,Slot.Mode.IN)).isNotNull
    }
  }
}