package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.converters.Converters
import javafx.util.StringConverter
import kotlin.reflect.KClass

data class ColumnProperty<T: Any, C: Any>(
  val type: KClass<C>,
  val getter: (T) -> C?,
  val converter: StringConverter<C> = Converters.converterFor(type)
)