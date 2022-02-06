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
package de.flapdoodle.kfx.layout.virtual

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ScrollBoundsTest {

    @Test
    fun itemIsSmallerThanWindowExceedingLowerBound() {
        val result = ScrollBounds.of(100.0, 50.0, -25.0, 0.0)

        assertThat(result)
            .isEqualTo(ScrollBounds(0.0, 75.0, 25.0))
    }

    @Test
    fun itemIsSmallerThanWindowAllVisible() {
        val result = ScrollBounds.of(100.0, 50.0, -25.0, 25.0)

        assertThat(result)
            .isEqualTo(ScrollBounds(25.0, 75.0, 25.0))
    }

    @Test
    fun itemIsSmallerThanWindowExceedingUpperBound() {
        val result = ScrollBounds.of(100.0, 50.0, -25.0, 100.0)

        assertThat(result)
            .isEqualTo(ScrollBounds(25.0, 100.0, 25.0))
    }

    @Test
    fun itemIsSameSizeThanWindowButWithOffset() {
        val result = ScrollBounds.of(100.0, 100.0, -25.0, 0.0)

        assertThat(result)
            .isEqualTo(ScrollBounds(0.0, 25.0, 0.0))
    }

    @Test
    fun itemIsSameSizeThanWindowLowerBoundsMatchs() {
        val result = ScrollBounds.of(100.0, 100.0, -25.0, 25.0)

        assertThat(result)
            .isEqualTo(ScrollBounds(25.0, 25.0, 0.0))
    }

    @Test
    fun itemIsBiggerThanWindow() {
        val result = ScrollBounds.of(100.0, 125.0, -25.0, 0.0)

        assertThat(result)
            .isEqualTo(ScrollBounds(0.0, 25.0, 20.0))
    }

    @Test
    fun itemIsBiggerThanWindowExceedingLowerLimit() {
        val result = ScrollBounds.of(100.0, 125.0, -25.0, -25.0)

        assertThat(result)
            .isEqualTo(ScrollBounds(-25.0, 25.0, 20.0))
    }

    @Test
    fun itemIsBiggerThanWindowExceedingUpperLimit() {
        val result = ScrollBounds.of(100.0, 125.0, -25.0, 50.0)

        assertThat(result)
            .isEqualTo(ScrollBounds(0.0, 50.0, 20.0))
    }
}
