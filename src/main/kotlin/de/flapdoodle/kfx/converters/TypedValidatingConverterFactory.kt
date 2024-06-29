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

open class TypedValidatingConverterFactory(
    private val entries: List<Entry<out Any>>
): ValidatingConverterFactory {

    data class Entry<T: Any>(
        val typeInfo: TypeInfo<T>,
        val factory: (Locale) -> ValidatingConverter<T>
    )

    override fun <S : Any> findConverter(s: TypeInfo<out S>, locale: Locale): ValidatingConverter<S>? {
        val factory = entries.firstOrNull { it.typeInfo == s }?.factory
        val converter = factory?.invoke(locale)

        @Suppress("UNCHECKED_CAST")
        return converter as ValidatingConverter<S>?
    }

    companion object {
        fun <T: Any> factory(clazz: KClass<T>, factory: (Locale) -> ValidatingConverter<T>): Entry<out Any> {
            return Entry(TypeInfo.of(clazz.javaObjectType), factory)
        }
    }
}