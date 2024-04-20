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
package de.flapdoodle.kfx.controls.charts

import java.time.LocalDate
import java.time.temporal.ChronoUnit

object RangeFactories {

    fun localDate(): RangeFactory<LocalDate> {
        return RangeFactory { values ->
            dateRange(values)
        }
    }

    fun number(): RangeFactory<Number> {
        return RangeFactory { values ->
            doubleRange(values)
        }
    }

    private fun dateRange(list: List<LocalDate>): Range<LocalDate> {
        if (list.isEmpty() || list.size==1) return empty()

        val min = list.min()
        val max = list.max()

        val dist = ChronoUnit.DAYS.between(min, max)

        return Range { value, scale ->
            val valueDist = ChronoUnit.DAYS.between(min, value)
            scale * valueDist / dist
        }
    }

    private fun doubleRange(list: List<Number>): Range<Number> {
        if (list.isEmpty() || list.size==1) return empty()

        val asDouble = list.map { it.toDouble() }
        val min = asDouble.min()
        val max = asDouble.max()

        val dist = max - min

        return Range { value, scale ->
            val valueDist = value.toDouble() - min
            scale * valueDist / dist
        }
    }

    private fun <T : Any> empty(): Range<T> {
        return Range { _, scale ->
            scale / 2.0
        }
    }
}