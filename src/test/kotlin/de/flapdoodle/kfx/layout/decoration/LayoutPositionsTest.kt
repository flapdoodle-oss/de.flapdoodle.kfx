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
package de.flapdoodle.kfx.layout.decoration

import de.flapdoodle.kfx.types.CardinalDirection
import javafx.geometry.BoundingBox
import javafx.geometry.Point2D
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LayoutPositionsTest {

  @Test
  fun eastWestOffset() {
    val offset = LayoutPositions.offset(
      sourceBounds = BoundingBox(10.0, 10.0, 30.0, 30.0),
      sourceOffset = LayoutPositions.Offset(CardinalDirection.EAST, 10.0),
      destinationBounds = BoundingBox(0.0, 0.0, 50.0, 50.0),
      destinationOffset = LayoutPositions.Offset(CardinalDirection.WEST, 10.0)
    )

    // middle of source east -> 40,25
    // x+10 to connection point -> 50,25
    // x+10 to dest west -> 60,25
    // x+0, y-25 to dest layoutXY: 60,0

    assertThat(offset)
      .isEqualTo(Point2D(60.0, 0.0))
  }

  @Test
  fun westEastOffset() {
    val offset = LayoutPositions.offset(
      sourceBounds = BoundingBox(10.0, 10.0, 30.0, 30.0),
      sourceOffset = LayoutPositions.Offset(CardinalDirection.WEST, 10.0),
      destinationBounds = BoundingBox(0.0, 0.0, 50.0, 50.0),
      destinationOffset = LayoutPositions.Offset(CardinalDirection.EAST, 10.0)
    )

    // middle of source west -> 10,25
    // x-10 to connection point -> 0,25
    // x-10 to dest west -> -10,25
    // x-50, y-25 to dest layoutXY: -60,0

    assertThat(offset)
      .isEqualTo(Point2D(-60.0, 0.0))
  }

  @Test
  fun sample() {
    // offset for BoundingBox [minX:0.0, minY:540.0, minZ:0.0, width:0.0, height:0.0, depth:0.0, maxX:0.0, maxY:540.0, maxZ:0.0]
    // to BoundingBox [minX:0.0, minY:0.0, minZ:0.0, width:16.5419921875, height:16.0, depth:0.0, maxX:16.5419921875, maxY:16.0, maxZ:0.0]:
    // Point2D [x = 16.5419921875, y = -532.0]
    val offset = LayoutPositions.offset(
      sourceBounds = BoundingBox(0.0, 540.0, 0.0, 0.0),
      sourceOffset = LayoutPositions.Offset(CardinalDirection.EAST, 0.0),
      destinationBounds = BoundingBox(0.0, 0.0, 16.5419921875, 16.0),
      destinationOffset = LayoutPositions.Offset(CardinalDirection.WEST, 0.0)
    )
    
    assertThat(offset)
      .isEqualTo(Point2D(0.0, 532.0))
  }
}