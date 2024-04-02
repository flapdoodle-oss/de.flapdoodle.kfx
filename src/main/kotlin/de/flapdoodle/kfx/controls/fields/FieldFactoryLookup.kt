package de.flapdoodle.kfx.controls.fields

import kotlin.reflect.KClass

interface FieldFactoryLookup {
  fun <T: Any> fieldFactory(type: KClass<T>): FieldFactory<T>
}