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

  fun <S : Any> validatingFor(s: KClass<out S>, locale: Locale): ValidatingConverter<S> {
    val type = s.javaPrimitiveType ?: s
    val converter = validatingConverters(locale).firstOrNull { it.first == type }
    return if (converter==null) {
      throw RuntimeException("not implemented for type:" + s.qualifiedName)
    } else {
      @Suppress("UNCHECKED_CAST")
      converter.second as ValidatingConverter<S>
    }
  }

  fun validatingConverters(locale: Locale) = listOf(
    Int::class to IntConverter(locale),
    Integer::class to IntConverter(locale),
    Integer::class.javaPrimitiveType to IntConverter(locale),
    Double::class to DoubleConverter(locale),
    Double::class.javaPrimitiveType to DoubleConverter(locale),
    Float::class to FloatConverter(locale),
    Float::class.javaPrimitiveType to FloatConverter(locale),
    Long::class to LongConverter(locale),
    Long::class.javaPrimitiveType to LongConverter(locale),
    Number::class to BigDecimalConverter(locale),
    BigDecimal::class to BigDecimalConverter(locale),
    BigInteger::class to BigIntegerConverter(locale),
    String::class to de.flapdoodle.kfx.converters.impl.StringConverter(),
    LocalDate::class to LocalDateConverter(locale),
    LocalDateTime::class to LocalDateTimeConverter(locale),
  )
}