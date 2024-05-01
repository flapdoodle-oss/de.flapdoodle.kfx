package de.flapdoodle.kfx.types

import de.flapdoodle.kfx.types.CardinalDirections.cardinalDirection
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CardinalDirectionsTest {

  @Test
  fun cardinalDirection() {
    assertThat(cardinalDirection(0.0)).isEqualTo(CardinalDirection.EAST)
    assertThat(cardinalDirection(45.0)).isEqualTo(CardinalDirection.NORTHEAST)
    assertThat(cardinalDirection(90.0)).isEqualTo(CardinalDirection.NORTH)
    assertThat(cardinalDirection(135.0)).isEqualTo(CardinalDirection.NORTHWEST)
    assertThat(cardinalDirection(180.0)).isEqualTo(CardinalDirection.WEST)
    assertThat(cardinalDirection(-45.0)).isEqualTo(CardinalDirection.SOUTHEAST)
    assertThat(cardinalDirection(-90.0)).isEqualTo(CardinalDirection.SOUTH)
    assertThat(cardinalDirection(-135.0)).isEqualTo(CardinalDirection.SOUTHWEST)
    assertThat(cardinalDirection(-180.0)).isEqualTo(CardinalDirection.WEST)

    assertThat(cardinalDirection(-179.0)).isEqualTo(CardinalDirection.WEST)
    assertThat(cardinalDirection(179.0)).isEqualTo(CardinalDirection.WEST)

    assertThat(cardinalDirection(179.0 + (10 * 360.0))).isEqualTo(CardinalDirection.WEST)
  }

}