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

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

object BigDecimalType : NumberType<BigDecimal> {
  override fun min(values: List<BigDecimal>): BigDecimal? {
    return values.minOrNull()
  }

  override fun max(values: List<BigDecimal>): BigDecimal? {
    return values.maxOrNull()
  }

  override fun offset(min: BigDecimal, max: BigDecimal, scale: Double, value: BigDecimal): Double {
    val valueDist = value - min
    val dist = max - min
    return if (dist != BigDecimal.ZERO)
      scale * (valueDist.divide(dist, MathContext.DECIMAL128)).toDouble()
    else
      scale / 2.0
  }

  override fun units(min: BigDecimal, max: BigDecimal): List<NumberUnit<BigDecimal>> {
    require(min <= max) { "$min > $max" }

    val dist = max - min
    val oneTickUnit = biggestOneTick(dist)
    return listOf(
      Unit(oneTickUnit),
      Unit(oneTickUnit.divide(BigDecimal.valueOf(5L))),
      Unit(oneTickUnit.divide(BigDecimal.TEN)),
      Unit(oneTickUnit.divide(BigDecimal.TEN).divide(BigDecimal.valueOf(5L))),
      Unit(oneTickUnit.divide(BigDecimal.TEN).divide(BigDecimal.TEN))
    )
  }

  private fun biggestOneTick(dist: BigDecimal, start: BigDecimal = BigDecimal.ONE): BigDecimal {
    return if (dist>start) {
      unitUntilDistIsSmaller(dist, start)
    } else {
      unitUntilDistIsBigger(dist, start)
    }
  }

  private fun unitUntilDistIsSmaller(dist: BigDecimal, start: BigDecimal): BigDecimal {
    return if (dist > start) {
      unitUntilDistIsSmaller(dist, start.multiply(BigDecimal.TEN))
    } else {
      start
    }
  }

  private fun unitUntilDistIsBigger(dist: BigDecimal, start: BigDecimal): BigDecimal {
    return if (dist < start) {
      unitUntilDistIsBigger(dist, start.divide(BigDecimal.TEN))
    } else {
      start
    }
  }

  data class Unit(val unit: BigDecimal) : NumberUnit<BigDecimal> {

    override fun unitsBetween(min: BigDecimal, max: BigDecimal): Int {
      val diff = firstUnit(max) - firstUnit(min)
      return (diff.divide(unit)).toInt()
    }

    override fun firstUnit(value: BigDecimal): BigDecimal {
      val rest = value % unit
      val onUnit = value - rest
      return if (rest>BigDecimal.ZERO) onUnit + unit else onUnit
    }

    override fun next(value: BigDecimal, offset: Int): BigDecimal {
      return value + (BigDecimal.valueOf(offset.toLong()).multiply(unit))
    }

  }
}