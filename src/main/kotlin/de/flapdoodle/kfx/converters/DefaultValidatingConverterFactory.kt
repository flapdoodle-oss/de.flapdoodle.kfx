package de.flapdoodle.kfx.converters

import de.flapdoodle.kfx.converters.impl.*
import de.flapdoodle.reflection.TypeInfo
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.reflect.KClass

object DefaultValidatingConverterFactory : ValidatingConverterFactory {

    private val validatingConverterFactories = listOf(
        factory(Int::class, ::IntConverter),
//    factory(Integer::class, ::IntConverter),
        factory(Double::class, ::DoubleConverter),
        factory(Float::class, ::FloatConverter),
        factory(Long::class, ::LongConverter),
//    factory(Number::class, ::BigDecimalConverter),
        factory(BigDecimal::class, ::BigDecimalConverter),
        factory(BigInteger::class, ::BigIntegerConverter),
        factory(String::class) { _ -> StringConverter() },
        factory(LocalDate::class, ::LocalDateConverter),
        factory(LocalDateTime::class, ::LocalDateTimeConverter),
    )

    private fun <T: Any> factory(clazz: KClass<T>, factory: (Locale) -> ValidatingConverter<T>): TypedValidatingConverterFactory<out Any> {
        return TypedValidatingConverterFactory(TypeInfo.of(clazz.javaObjectType), factory)
    }

    override fun <S : Any> findConverter(s: TypeInfo<out S>, locale: Locale): ValidatingConverter<S>? {
        val factory = validatingConverterFactories.firstOrNull { it.typeInfo == s }?.factory
        val converter = factory?.invoke(locale)

        @Suppress("UNCHECKED_CAST")
        return converter as ValidatingConverter<S>?
    }
}