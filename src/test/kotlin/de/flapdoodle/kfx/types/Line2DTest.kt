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

import de.flapdoodle.kfx.isEqualTo
import javafx.geometry.Point2D
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Line2DTest {

    @Test
    fun leftToRight() {
        val sample = Point2D.ZERO.to(Point2D(10.0, 0.0))

        assertThat(sample.positionAt(UnitInterval.ZERO, 0.5, 0.0))
            .isEqualTo(Point2D(0.0, -0.5), 0.01)
        assertThat(sample.positionAt(UnitInterval(0.5), 0.5, 0.0))
            .isEqualTo(Point2D(5.0, -0.5), 0.01)
        assertThat(sample.positionAt(UnitInterval.ONE, 0.5, 0.0))
            .isEqualTo(Point2D(10.0, -0.5), 0.01)
    }

    @Test
    fun leftToRightMustOffsetY() {
        val sample = Point2D.ZERO.to(Point2D(10.0, 0.0))
        val result = sample.positionAt(UnitInterval.ONE, 0.5, 0.0)

        assertThat(result).isEqualTo(Point2D(10.0, -0.5), 0.01)
    }

    @Test
    fun topToBottomMustOffsetRight() {
        val sample = Point2D.ZERO.to(Point2D(0.0, 10.0))
        val result = sample.positionAt(UnitInterval.ONE, 0.5, 0.0)

        assertThat(result).isEqualTo(Point2D(0.5, 10.0), 0.01)
    }
}