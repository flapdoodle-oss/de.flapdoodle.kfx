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
import java.math.BigInteger
import kotlin.reflect.KClass

interface NumberType<T: Number> {
  fun min(values: List<T>): T?
  fun max(values: List<T>): T?
  fun offset(min: T, max: T, scale: Double, value: T): Double
  
  fun units(min: T, max: T): List<NumberUnit<T>>

  companion object {
    fun <T: Number> of(type: KClass<T>): NumberType<T> {
      return when (type) {
        BigDecimal::class -> BigDecimalType
        Double::class -> DoubleType

        BigInteger::class -> BigIntType
        Long::class -> LongType
        Int::class -> IntType
        else -> throw IllegalArgumentException("type not supported: $type")
      } as NumberType<T>
    }
  }
}