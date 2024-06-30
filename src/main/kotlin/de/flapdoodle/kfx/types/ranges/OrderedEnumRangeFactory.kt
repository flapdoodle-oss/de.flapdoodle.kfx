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

import java.util.EnumSet
import kotlin.reflect.KClass

class OrderedEnumRangeFactory<T: Enum<T>>(
  val enumType: KClass<T>
) : RangeFactory<T> {

  private val all = EnumSet.allOf(enumType.java).sorted()

  override fun rangeOf(values: List<T>): Range<T> {
    if (values.isEmpty()) return Range.empty()
    if (values.size == 1) return Range.single(values[0])

    val sorted = values.toSortedSet()
    val minOrdinal = sorted.first().ordinal
    val maxOrdinal = sorted.last().ordinal
    val dist = maxOrdinal - minOrdinal + 2

    val ticks = Ticks(list = all.subList(minOrdinal, maxOrdinal + 1))

    return object : Range<T> {
      override fun offset(value: T, scale: Double): Double {
        return (scale * (value.ordinal - minOrdinal + 1)) / dist;
      }

      override fun ticks(maxTicks: Int): List<Ticks<T>> {
        return if (maxTicks>=ticks.list.size)
          listOf(ticks)
        else
          emptyList()
      }
    }
  }

}