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

import de.flapdoodle.kfx.types.BoundingBoxes
import javafx.geometry.BoundingBox
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer

internal class BoundingBoxesTest {

    @Test
    fun emptyBounds() {
        assertThat(BoundingBoxes.empty().isEmpty).isTrue()
    }

    @Test
    fun mergeTwoEmptyBoxesGivesEmpty() {
        val one = BoundingBoxes.empty()
        val two = BoundingBoxes.empty()
        assertThat(BoundingBoxes.merge(one, two).isEmpty).isTrue()
    }

    @Test
    fun mergeOneEmptyBoxesGivesOther() {
        val empty = BoundingBoxes.empty()
        val valid = BoundingBox(1.0, 2.0, 3.0, 10.0, 20.0, 30.0)
        val emptyFirst = ThreadLocalRandom.current().nextBoolean()

        val testee = if (emptyFirst) BoundingBoxes.merge(empty, valid) else BoundingBoxes.merge(valid, empty)

        assertThat(testee).satisfies(Consumer {
            assertThat(it.minX).isEqualTo(1.0)
            assertThat(it.minY).isEqualTo(2.0)
            assertThat(it.minZ).isEqualTo(3.0)
            assertThat(it.width).isEqualTo(10.0)
            assertThat(it.height).isEqualTo(20.0)
            assertThat(it.depth).isEqualTo(30.0)
        })
    }

    @Test
    fun mergeTwoExpandsEachDirection() {
        val one = BoundingBox(0.0, 2.0, 3.0, 10.0, 20.0, 30.0)
        val two = BoundingBox(1.0, 0.0, 3.0, 10.0, 20.0, 35.0)

        val testee = BoundingBoxes.merge(one, two)

        assertThat(testee).satisfies(Consumer {
            assertThat(it.minX).isEqualTo(0.0)
            assertThat(it.minY).isEqualTo(0.0)
            assertThat(it.minZ).isEqualTo(3.0)
            assertThat(it.width).isEqualTo(11.0)
            assertThat(it.height).isEqualTo(22.0)
            assertThat(it.depth).isEqualTo(35.0)
        })
    }
}