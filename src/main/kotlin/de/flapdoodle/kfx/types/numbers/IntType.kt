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
package de.flapdoodle.kfx.types.numbers

object IntType : NumberType<Int> {
  override fun min(values: List<Int>): Int? {
    return values.minOrNull()
  }

  override fun max(values: List<Int>): Int? {
    return values.maxOrNull()
  }

  override fun offset(min: Int, max: Int, scale: Double, value: Int): Double {
    require(min <= max) { "$min > $max" }

    val valueDist = value - min
    val dist = max - min
    return if (dist != 0)
      scale * valueDist / dist
    else
      scale / 2.0
  }

  override fun units(min: Int, max: Int): List<NumberUnit<Int>> {
    require(min <= max) { "$min > $max" }

    val dist = max - min
    val oneTickUnit = biggestOneTick(dist)
    return listOf(
      Unit(oneTickUnit),
      Unit(oneTickUnit / 5),
      Unit(oneTickUnit / 10),
      Unit(oneTickUnit / 50),
      Unit(oneTickUnit / 100)
    ).filter { it.unit != 0 }
  }

  private fun biggestOneTick(dist: Int): Int {
    return if (dist > 1L) {
      unitUntilDistIsSmaller(dist, 1)
    } else {
      unitUntilDistIsBigger(dist, 1)
    }
  }

  private fun unitUntilDistIsSmaller(dist: Int, start: Int): Int {
    return if (dist > start) {
      unitUntilDistIsSmaller(dist, start * 10)
    } else {
      start
    }
  }

  private fun unitUntilDistIsBigger(dist: Int, start: Int): Int {
    return if (dist < start) {
      unitUntilDistIsBigger(dist, start / 10)
    } else {
      start
    }
  }

  data class Unit(val unit: Int) : NumberUnit<Int> {

    override fun unitsBetween(min: Int, max: Int): Int {
      val diff = firstUnit(max) - firstUnit(min)
      return (diff / unit)
    }

    override fun firstUnit(value: Int): Int {
      val rest = value % unit
      val onUnit = value - rest
      return if (rest> 0) onUnit + unit else onUnit
    }

    override fun next(value: Int, offset: Int): Int {
      return value + (offset * unit)
    }
  }
}