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

import de.flapdoodle.reflection.TypeInfo
import java.util.*
import kotlin.reflect.KClass

interface ValidatingConverterFactory {
    fun <S : Any> findConverter(s: TypeInfo<out S>, locale: Locale): ValidatingConverter<S>?

    fun <S : Any> findConverter(s: KClass<out S>, locale: Locale): ValidatingConverter<S>? {
        return findConverter(TypeInfo.of(s.javaObjectType), locale)
    }

    fun <S : Any> converter(s: TypeInfo<out S>, locale: Locale): ValidatingConverter<S> {
        return findConverter(s, locale) ?: throw RuntimeException("not implemented for type:$s")
    }

    fun <S : Any> converter(s: KClass<out S>, locale: Locale): ValidatingConverter<S> {
        return converter(TypeInfo.of(s.javaObjectType), locale)
    }

    fun or(fallback: ValidatingConverterFactory): ValidatingConverterFactory {
        return FallbackFactory(this, fallback)
    }

    data class FallbackFactory(
        val primary: ValidatingConverterFactory,
        val fallback: ValidatingConverterFactory
    ) : ValidatingConverterFactory {
        override fun <S : Any> findConverter(s: TypeInfo<out S>, locale: Locale): ValidatingConverter<S>? {
            return primary.findConverter(s,locale) ?: fallback.findConverter(s, locale)
        }
    }
}