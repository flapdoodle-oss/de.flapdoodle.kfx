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
package de.flapdoodle.kfx.extensions

import de.flapdoodle.kfx.types.Point2DMath
import de.flapdoodle.kfx.types.Point2DMath.angle
import javafx.geometry.Point2D
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Percentage
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.sin

class Point2DMathTest {
  @Test
  fun angle() {
    val center = randomPoint()
    val delta = ThreadLocalRandom.current().nextDouble(1.0, 10.0)

    assertThat(angle(center, center.add(delta, 0.0))).isCloseTo(0.0, Percentage.withPercentage(99.0))
    assertThat(angle(center, center.add(delta, delta))).isCloseTo(45.0, Percentage.withPercentage(99.0))
    assertThat(angle(center, center.add(0.0, delta))).isCloseTo(90.0, Percentage.withPercentage(99.0))
    assertThat(angle(center, center.add(-delta, delta))).isCloseTo(135.0, Percentage.withPercentage(99.0))
    assertThat(angle(center, center.add(-delta, 0.0))).isCloseTo(180.0, Percentage.withPercentage(99.0))
    assertThat(angle(center, center.add(-delta, -delta))).isCloseTo(-135.0, Percentage.withPercentage(99.0))
    assertThat(angle(center, center.add(0.0, -delta))).isCloseTo(-90.0, Percentage.withPercentage(99.0))
    assertThat(angle(center, center.add(delta, -delta))).isCloseTo(-45.0, Percentage.withPercentage(99.0))
  }

  @Test
  @Disabled
  fun pointAt() {
    val center = randomPoint()

    val delta45 = sin(Math.toRadians(45.0)) * 10.0

    assertThat(Point2DMath.pointAt(center, 0.0, 10.0)).isEqualTo(center.add(10.0, 0.0))
    assertThat(Point2DMath.pointAt(center, 45.0, 10.0)).isEqualTo(center.add(delta45, delta45))
    assertThat(Point2DMath.pointAt(center, 0.0, 10.0, 10.0)).isEqualTo(center.add(10.0, 10.0))
    assertThat(Point2DMath.pointAt(center, 90.0, 10.0, 10.0)).isEqualTo(center.add(-10.0, 10.0))
  }

  fun randomPoint(): Point2D {
    val current = ThreadLocalRandom.current()
    return Point2D(current.nextDouble(-100.0, 100.0), current.nextDouble(-100.0, 100.0))
  }
}