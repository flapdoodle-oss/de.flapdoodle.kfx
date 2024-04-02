package de.flapdoodle.kfx.controls.bettertable.fields

import de.flapdoodle.kfx.converters.Converters
import kotlin.reflect.KClass

object DefaultFieldFactoryLookup : FieldFactoryLookup {

  override fun <T : Any> fieldFactory(type: KClass<T>): FieldFactory<T> {
    return TextFieldFactory(Converters.converterFor(type))
  }
}