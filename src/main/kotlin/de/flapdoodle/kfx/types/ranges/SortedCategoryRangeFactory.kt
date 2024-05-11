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

class SortedCategoryRangeFactory<T: Comparable<T>> : RangeFactory<T> {
  override fun rangeOf(values: List<T>): Range<T> {
    val sorted = values.toSortedSet()
    val parts = sorted.size + 1
    val ticks = Ticks(list = sorted.toList())

    return object : Range<T> {
      override fun offset(value: T, scale: Double): Double {
        val index = sorted.indexOf(value)
        return (scale * (index + 1)) / parts;
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