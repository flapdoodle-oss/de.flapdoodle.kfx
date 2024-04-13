package de.flapdoodle.kfx.controls.colors

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class HashedColorsTest {
    @Test
    fun hashedColorMustAlwaysGetOne() {
        (0..1000).forEach {
            val name = UUID.randomUUID().toString()
            Assertions.assertThat(HashedColors.hashedColor(name.hashCode())).isNotNull
        }
    }

}