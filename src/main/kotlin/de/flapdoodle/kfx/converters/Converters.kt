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
import de.flapdoodle.reflection.TypeInfo
import javafx.util.StringConverter
import javafx.util.converter.*
import java.io.Serializable
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.reflect.KClass

object Converters {
  private val stringConverters: Map<TypeInfo<out Any>, StringConverter<out Serializable>> = listOf(
    Int::class to IntegerStringConverter(),
    Integer::class to IntegerStringConverter(),
    Double::class to DoubleStringConverter(),
    Float::class to FloatStringConverter(),
    Long::class to LongStringConverter(),
    Number::class to NumberStringConverter(),
    BigDecimal::class to BigDecimalStringConverter(),
    BigInteger::class to BigIntegerStringConverter(),
    String::class to DefaultStringConverter(),
    LocalDate::class to LocalDateStringConverter(),
    LocalTime::class to LocalTimeStringConverter(),
    LocalDateTime::class to LocalDateTimeStringConverter(),
  ).associate { TypeInfo.of(it.first.javaObjectType) to it.second }

  private fun validatingConverters(locale: Locale) = listOf<Pair<KClass<out Any>, ValidatingConverter<out Any>>>(
    Int::class to IntConverter(locale),
    Integer::class to IntConverter(locale),
    Double::class to DoubleConverter(locale),
    Float::class to FloatConverter(locale),
    Long::class to LongConverter(locale),
    Number::class to BigDecimalConverter(locale),
    BigDecimal::class to BigDecimalConverter(locale),
    BigInteger::class to BigIntegerConverter(locale),
    String::class to de.flapdoodle.kfx.converters.impl.StringConverter(),
    LocalDate::class to LocalDateConverter(locale),
    LocalDateTime::class to LocalDateTimeConverter(locale),
  ).associate { TypeInfo.of(it.first.javaObjectType) to it.second }


  fun <S : Any> converterFor(s: KClass<out S>): StringConverter<S> {
    return converterFor(TypeInfo.of(s.javaObjectType))
  }

  fun <S : Any> converterFor(s: TypeInfo<out S>): StringConverter<S> {
    val converter = stringConverters[s]
      ?: throw RuntimeException("not implemented for type:" + s)

    @Suppress("UNCHECKED_CAST")
    return converter as StringConverter<S>
  }

  fun <S : Any> validatingFor(s: KClass<out S>, locale: Locale): ValidatingConverter<S> {
    return validatingFor(TypeInfo.of(s.javaObjectType), locale)
  }

  fun <S : Any> validatingFor(s: TypeInfo<out S>, locale: Locale): ValidatingConverter<S> {
    val converter = validatingConverters(locale)[s]
      ?: throw RuntimeException("not implemented for type:" + s)

    @Suppress("UNCHECKED_CAST")
    return converter as ValidatingConverter<S>
  }
}