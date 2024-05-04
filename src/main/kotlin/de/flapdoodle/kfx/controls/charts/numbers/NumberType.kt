package de.flapdoodle.kfx.controls.charts.numbers

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