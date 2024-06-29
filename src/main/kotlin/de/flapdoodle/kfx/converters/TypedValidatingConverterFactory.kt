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