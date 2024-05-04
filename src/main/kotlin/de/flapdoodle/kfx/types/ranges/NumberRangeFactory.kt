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

import de.flapdoodle.kfx.types.numbers.NumberType
import de.flapdoodle.kfx.types.numbers.NumberUnit
import kotlin.reflect.KClass

class NumberRangeFactory<T: Number>(
  private val type: KClass<T>
): RangeFactory<T> {
  private val numberType = NumberType.of(type)

  override fun rangeOf(values: List<T>): Range<T> {
    return doubleRange(numberType, values)
  }
  
  companion object {
    private fun <T: Number> doubleRange(type: NumberType<T>, list: List<T>): Range<T> {
      if (list.isEmpty()) return Range.empty()
      if (list.size == 1) return Range.single(list[0])


      val min = requireNotNull(type.min(list)) { "min not found for $list"}
      val max = requireNotNull(type.max(list)) { "max not found for $list"}

      return object : Range<T> {
        override fun offset(value: T, scale: Double): Double {
          return type.offset(min, max, scale, value)
        }

        override fun ticks(maxTicks: Int): List<Ticks<T>> {
          return type.units(min, max)
            .map { ticks(it, min, max, maxTicks) }
            .filter { it.list.isNotEmpty() }
            .reversed()
        }
      }
    }

    // VisibleForTesting
    internal fun <T: Number> ticks(unit: NumberUnit<T>, min: T, max: T, maxTicks: Int): Ticks<T> {
      val ticks = unit.unitsBetween(min, max)
      val list = if (ticks in 1..maxTicks) {
        val start = unit.firstUnit(min)
        (0..<ticks).map {
          unit.next(start, it)
        }
      } else {
        emptyList()
      }
      return Ticks(list)
    }
  }

}