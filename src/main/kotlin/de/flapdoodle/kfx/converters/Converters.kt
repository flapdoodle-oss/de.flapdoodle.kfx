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
package de.flapdoodle.kfx.converters

import de.flapdoodle.kfx.converters.impl.*
import javafx.util.StringConverter
import javafx.util.converter.*
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.reflect.KClass

object Converters {
  fun <S : Any> converterFor(s: KClass<out S>): StringConverter<S> {
    @Suppress("UNCHECKED_CAST")
    return when (s.javaPrimitiveType ?: s) {
      Int::class -> IntegerStringConverter()
      Integer::class -> IntegerStringConverter()
      Integer::class.javaPrimitiveType -> IntegerStringConverter()
      Double::class -> DoubleStringConverter()
      Double::class.javaPrimitiveType -> DoubleStringConverter()
      Float::class -> FloatStringConverter()
      Float::class.javaPrimitiveType -> FloatStringConverter()
      Long::class -> LongStringConverter()
      Long::class.javaPrimitiveType -> LongStringConverter()
      Number::class -> NumberStringConverter()
      BigDecimal::class -> BigDecimalStringConverter()
      BigInteger::class -> BigIntegerStringConverter()
      String::class -> DefaultStringConverter()
      LocalDate::class -> LocalDateStringConverter()
      LocalTime::class -> LocalTimeStringConverter()
      LocalDateTime::class -> LocalDateTimeStringConverter()
//      Boolean::class.javaPrimitiveType -> {
//        (this as TableColumn<T, Boolean?>).useCheckbox(true)
//      }
      else -> throw RuntimeException("not implemented for type:" + s.qualifiedName)
    } as StringConverter<S>
  }

  fun <S: Any> validatingFor(s: KClass<out S>, locale: Locale): ValidatingConverter<S> {
    @Suppress("UNCHECKED_CAST")
    return when (s.javaPrimitiveType ?: s) {
      Int::class -> IntConverter(locale)
      Integer::class -> IntConverter(locale)
      Integer::class.javaPrimitiveType -> IntConverter(locale)
      Double::class -> DoubleConverter(locale)
      Double::class.javaPrimitiveType -> DoubleConverter(locale)
      Float::class -> FloatConverter(locale)
      Float::class.javaPrimitiveType -> FloatConverter(locale)
      Long::class -> LongConverter(locale)
      Long::class.javaPrimitiveType -> LongConverter(locale)
      Number::class -> BigDecimalConverter(locale)
      BigDecimal::class -> BigDecimalConverter(locale)
      BigInteger::class -> BigIntegerConverter(locale)
      String::class -> de.flapdoodle.kfx.converters.impl.StringConverter()
      LocalDate::class -> LocalDateConverter(locale)
      LocalDateTime::class -> LocalDateTimeConverter(locale)

      else -> throw RuntimeException("not implemented for type:" + s.qualifiedName)
    } as ValidatingConverter<S>
  }
}