/*
 * Copyright (C) 2022
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.kfx.types

import de.flapdoodle.kfx.types.CardinalDirections.cardinalDirection
import org.assertj.core.api.Assertions.assertThat
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