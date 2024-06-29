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
        return findConverter(s, locale) ?: throw RuntimeException("not implemented for type:" + s)
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