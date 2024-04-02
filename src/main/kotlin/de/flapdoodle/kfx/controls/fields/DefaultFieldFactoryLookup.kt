package de.flapdoodle.kfx.controls.fields

import de.flapdoodle.kfx.converters.Converters
import java.time.LocalDate
import kotlin.reflect.KClass

object DefaultFieldFactoryLookup : FieldFactoryLookup {

  override fun <T : Any> fieldFactory(type: KClass<T>): FieldFactory<T> {
    if (LocalDate::class == type) {
      return DatePickerFieldFactory() as FieldFactory<T>
    }
    return TextFieldFactory(Converters.converterFor(type))
  }
}