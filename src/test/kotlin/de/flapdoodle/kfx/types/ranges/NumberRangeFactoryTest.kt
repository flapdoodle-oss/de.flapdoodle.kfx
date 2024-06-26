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
package de.flapdoodle.kfx.types.ranges

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NumberRangeFactoryTest {

  @Test
  fun ticks() {
    val ticks = NumberRangeFactory(Double::class).rangeOf(listOf(-2.01, 8.01)).ticks(14)

    assertThat(ticks)
      .hasSize(5)

    assertThat(ticks[0].list)
      .containsExactly(-2.0, -1.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0)
    assertThat(ticks[1].list)
      .containsExactly(-2.0, 0.0, 2.0, 4.0, 6.0, 8.0)
    assertThat(ticks[2].list)
      .containsExactly(0.0)
    assertThat(ticks[3].list)
      .containsExactly(0.0)
    assertThat(ticks[4].list)
      .containsExactly(0.0)
  }
}