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

import java.math.BigInteger

object BigIntType : NumberType<BigInteger> {
  override fun min(values: List<BigInteger>): BigInteger? {
    return values.minOrNull()
  }

  override fun max(values: List<BigInteger>): BigInteger? {
    return values.maxOrNull()
  }

  override fun offset(min: BigInteger, max: BigInteger, scale: Double, value: BigInteger): Double {
    val valueDist = value - min
    val dist = max - min
    return if (dist != BigInteger.ZERO)
      (valueDist.multiply(BigInteger.valueOf(scale.toLong()).divide(dist))).toDouble()
    else
      scale / 2.0
  }

  override fun units(min: BigInteger, max: BigInteger): List<NumberUnit<BigInteger>> {
    require(min <= max) { "$min > $max" }

    val dist = max - min
    val oneTickUnit = biggestOneTick(dist)
    return listOf(
      Unit(oneTickUnit),
      Unit(oneTickUnit.divide(BigInteger.valueOf(5L))),
      Unit(oneTickUnit.divide(BigInteger.TEN)),
      Unit(oneTickUnit.divide(BigInteger.TEN).divide(BigInteger.valueOf(5L))),
      Unit(oneTickUnit.divide(BigInteger.TEN).divide(BigInteger.TEN))
    ).filter { it.unit != BigInteger.ZERO }
  }

  private fun biggestOneTick(dist: BigInteger, start: BigInteger = BigInteger.ONE): BigInteger {
    return if (dist > start) {
      unitUntilDistIsSmaller(dist, start)
    } else {
      unitUntilDistIsBigger(dist, start)
    }
  }

  private fun unitUntilDistIsSmaller(dist: BigInteger, start: BigInteger): BigInteger {
    return if (dist > start) {
      unitUntilDistIsSmaller(dist, start.multiply(BigInteger.TEN))
    } else {
      start
    }
  }

  private fun unitUntilDistIsBigger(dist: BigInteger, start: BigInteger): BigInteger {
    return if (dist < start) {
      unitUntilDistIsBigger(dist, start.divide(BigInteger.TEN))
    } else {
      start
    }
  }

  data class Unit(val unit: BigInteger) : NumberUnit<BigInteger> {

    override fun unitsBetween(min: BigInteger, max: BigInteger): Int {
      val diff = firstUnit(max) - firstUnit(min)
      return (diff.divide(unit)).toInt()
    }

    override fun firstUnit(value: BigInteger): BigInteger {
      val rest = value % unit
      val onUnit = value - rest
      return if (rest> BigInteger.ZERO) onUnit + unit else onUnit
    }

    override fun next(value: BigInteger, offset: Int): BigInteger {
      return value + (BigInteger.valueOf(offset.toLong()).multiply(unit))
    }

  }
}