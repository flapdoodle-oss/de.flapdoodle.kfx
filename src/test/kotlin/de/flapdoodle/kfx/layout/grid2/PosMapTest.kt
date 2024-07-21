package de.flapdoodle.kfx.layout.grid2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PosMapTest {
  @Test
  fun `map columns must give matching entries`() {
    val testee = PositionMap(mapOf(
      "(0,0)" to Pos(0, 0),
      "(1,0)" to Pos(1, 0),
      "(2,2)" to Pos(2, 2),
      "(1..2,2)" to Pos(1, 2, 2, 1)
    ))

    assertThat(testee.rows())
      .containsExactly(0, 2)

    assertThat(testee.columns())
      .containsExactly(0, 1, 2)

    //val result = testee.mapColumns { _, list -> list.joinToString(separator = "|") }

//    assertThat(result)
//      .containsExactly("(0,0)","(1,0)|(1,1)")
  }

}