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

object LongType : NumberType<Long> {
  override fun min(values: List<Long>): Long? {
    return values.minOrNull()
  }

  override fun max(values: List<Long>): Long? {
    return values.maxOrNull()
  }

  override fun offset(min: Long, max: Long, scale: Double, value: Long): Double {
    require(min <= max) { "$min > $max" }

    val valueDist = value - min
    val dist = max - min
    return if (dist != 0L)
      scale * valueDist / dist
    else
      scale / 2.0
  }

  override fun units(min: Long, max: Long): List<NumberUnit<Long>> {
    require(min <= max) { "$min > $max" }

    val dist = max - min
    val oneTickUnit = biggestOneTick(dist)
    return listOf(
      Unit(oneTickUnit),
      Unit(oneTickUnit / 5),
      Unit(oneTickUnit / 10),
      Unit(oneTickUnit / 50),
      Unit(oneTickUnit / 100)
    ).filter { it.unit != 0L }
  }

  private fun biggestOneTick(dist: Long): Long {
    return if (dist > 1L) {
      unitUntilDistIsSmaller(dist, 1L)
    } else {
      unitUntilDistIsBigger(dist, 1L)
    }
  }

  private fun unitUntilDistIsSmaller(dist: Long, start: Long): Long {
    return if (dist > start) {
      unitUntilDistIsSmaller(dist, start * 10)
    } else {
      start
    }
  }

  private fun unitUntilDistIsBigger(dist: Long, start: Long): Long {
    return if (dist < start) {
      unitUntilDistIsBigger(dist, start / 10)
    } else {
      start
    }
  }

  data class Unit(val unit: Long) : NumberUnit<Long> {

    override fun unitsBetween(min: Long, max: Long): Int {
      return ((max - min) / unit).toInt()
    }

    override fun firstUnit(value: Long): Long {
      val rest = value % unit
      return if (rest == 0L) value else value + (unit - rest)
    }

    override fun next(value: Long, offset: Int): Long {
      return value + (offset * unit)
    }
  }
}