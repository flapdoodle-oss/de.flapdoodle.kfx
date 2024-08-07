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
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.reflect.KClass

object DefaultValidatingConverterFactory : TypedValidatingConverterFactory(listOf(
    factory(Int::class, ::IntConverter),
    factory(Double::class, ::DoubleConverter),
    factory(Float::class, ::FloatConverter),
    factory(Long::class, ::LongConverter),
//    factory(Number::class, ::BigDecimalConverter),
    factory(BigInteger::class, ::BigIntegerConverter),
    factory(BigDecimal::class, ::BigDecimalConverter),
    factory(String::class) { _ -> StringConverter() },
    factory(LocalDate::class, ::LocalDateConverter),
    factory(LocalDateTime::class, ::LocalDateTimeConverter),
)) {
}