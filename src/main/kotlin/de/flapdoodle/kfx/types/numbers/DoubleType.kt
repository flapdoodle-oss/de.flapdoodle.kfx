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

object DoubleType : NumberType<Double> {
  override fun min(values: List<Double>): Double? {
    return values.minOrNull()
  }

  override fun max(values: List<Double>): Double? {
    return values.maxOrNull()
  }

  override fun offset(min: Double, max: Double, scale: Double, value: Double): Double {
    require(min <= max) { "$min > $max" }

    val valueDist = value - min
    val dist = max - min
    return if (dist != 0.0)
      scale * valueDist / dist
    else
      scale / 2.0
  }

  override fun units(min: Double, max: Double): List<NumberUnit<Double>> {
    require(min <= max) { "$min > $max" }

    val dist = max - min
    val oneTickUnit = biggestOneTick(dist)
    return listOf(
      Unit(oneTickUnit),
      Unit(oneTickUnit/5.0),
      Unit(oneTickUnit/10.0),
      Unit(oneTickUnit/50.0),
      Unit(oneTickUnit/100.0)
    )
  }

  private fun biggestOneTick(dist: Double): Double {
    val start = 1.0
    return if (dist>start) {
      unitUntilDistIsSmaller(dist, start)
    } else {
      unitUntilDistIsBigger(dist, start)
    }
  }

  private fun unitUntilDistIsSmaller(dist: Double, start: Double): Double {
    return if (dist > start) {
      unitUntilDistIsSmaller(dist, start * 10.0)
    } else {
      start
    }
  }

  private fun unitUntilDistIsBigger(dist: Double, start: Double): Double {
    return if (dist < start) {
      unitUntilDistIsBigger(dist, start / 10.0)
    } else {
      start
    }
  }

  data class Unit(val unit: Double) : NumberUnit<Double> {

    override fun unitsBetween(min: Double, max: Double): Int {
      val diff = firstUnit(max) - firstUnit(min)
      return (diff / unit).toInt()
    }

    override fun firstUnit(value: Double): Double {
      val rest = value % unit
      val onUnit = value - rest
      return if (rest>0.0) onUnit + unit else onUnit
    }

    override fun next(value: Double, offset: Int): Double {
      return value + (offset * unit)
    }

  }
}