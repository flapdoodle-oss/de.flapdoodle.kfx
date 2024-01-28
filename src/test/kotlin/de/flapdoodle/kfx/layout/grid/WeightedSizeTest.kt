/**
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
package de.flapdoodle.kfx.layout.grid

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class WeightedSizeTest {

    @Test
    fun `same weights must share space equal`() {
        val src = listOf(
            WeightedSize(weight = 1.0, min = 0.0, max = Double.MAX_VALUE),
            WeightedSize(weight = 1.0, min = 0.0, max = Double.MAX_VALUE)
        )

        val result = WeightedSize.distribute(100.0, src)

        assertThat(result).containsExactly(50.0, 50.0)
    }

    @Test
    fun `same weights must share space equal if enough space left`() {
        val src = listOf(
            WeightedSize(weight = 1.0, min = 75.0, max = Double.MAX_VALUE),
            WeightedSize(weight = 1.0, min = 0.0, max = Double.MAX_VALUE)
        )

        val result = WeightedSize.distribute(100.0, src)

        assertThat(result).containsExactly(75.0, 25.0)
    }

    @Test
    fun `same weights must share space equal if not too much space left`() {
        val src = listOf(
            WeightedSize(weight = 1.0, min = 0.0, max = 25.0),
            WeightedSize(weight = 1.0, min = 0.0, max = Double.MAX_VALUE)
        )

        val result = WeightedSize.distribute(100.0, src)

        assertThat(result).containsExactly(25.0, 75.0)
    }

    @Test
    fun `sample`() {
        val src = listOf(
            WeightedSize(weight = 1.0, min = 20.0, max = 30.0),
            WeightedSize(weight = 4.0, min = 10.0, max = Double.MAX_VALUE),
            WeightedSize(weight = 1.0, min = 10.0, max = 60.0)
        )

//    assertThat(WeightedSize.distribute(400.0, src)).containsExactly(30.0, 310.0, 60.0)
        assertThat(WeightedSize.distribute(330.0, src)).containsExactly(30.0, 240.0, 60.0)

        (300..400).forEach {
            val space = it * 1.0
            val sizes = WeightedSize.distribute(space, src)
            assertThat(sizes.sumByDouble { it })
                .describedAs("space = $space")
                .isEqualTo(space)
        }
//    assertThat(WeightedSize.distribute(300.0, src)).containsExactly(30.0, 310.0, 60.0)
    }

    @Test
    fun `sample bug`() {
        val src = listOf(
            WeightedSize(weight = 4.0, min = 24.0, max = 24.0),
            WeightedSize(weight = 1.0, min = 24.0, max = 100.0)
        )

        assertThat(WeightedSize.distribute(100.0, src)).containsExactly(24.0, 100.0-24.0)
        assertThat(WeightedSize.distribute(400.0, src)).containsExactly(24.0, 100.0)

        // 87x148
    }

    @Test
    fun `sample bug2`() {
        val src = listOf(
            WeightedSize(weight = 1.0, min = 43.0, max = 43.0),
            WeightedSize(weight = 2.0, min = 43.0, max = 100.0)
        )

        assertThat(WeightedSize.distribute(87.0, src)).containsExactly(43.0, 44.0)
        assertThat(WeightedSize.distribute(88.0, src)).containsExactly(43.0, 45.0)

        // 87x148
    }

}