package de.flapdoodle.kfx.converters

import de.flapdoodle.reflection.TypeInfo
import java.util.Locale

data class TypedValidatingConverterFactory<T: Any>(
  val typeInfo: TypeInfo<T>,
  val factory: (Locale) -> ValidatingConverter<T>
)