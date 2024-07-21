package de.flapdoodle.kfx.layout.grid

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PosMapTest {
  @Test
  fun `map columns must give matching entries`() {
    val testee = PositionMap(
      mapOf(
        "(0,0)" to Pos(0, 0),
        "(1,0)" to Pos(1, 0),
        "(2,2)" to Pos(2, 2),
        "(1..2,2)" to Pos(1, 2, 2, 1)
      )
    )

    assertThat(testee.rows())
      .containsExactly(0, 1, 2)

    assertThat(testee.columns())
      .containsExactly(0, 1, 2)

    val mapColumns = testee.mapColumns { _, list -> list.keys.joinToString(separator = "|") }

    assertThat(mapColumns)
      .containsExactly("(0,0)", "(1,0)|(1..2,2)", "(2,2)|(1..2,2)")

    val mapRows = testee.mapRows { _, list -> list.keys.joinToString(separator = "|") }

    assertThat(mapRows)
      .containsExactly("(0,0)|(1,0)", "", "(2,2)|(1..2,2)")
  }

}