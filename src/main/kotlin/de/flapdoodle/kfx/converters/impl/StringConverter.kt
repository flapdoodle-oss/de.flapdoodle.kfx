package de.flapdoodle.kfx.converters.impl

import de.flapdoodle.kfx.converters.ValidatingConverter
import de.flapdoodle.kfx.converters.ValueOrError

class StringConverter : ValidatingConverter<String> {
  override fun toString(value: String): String {
    return value
  }

  override fun fromString(value: String): ValueOrError<String> {
    return ValueOrError.Value(value)
  }
}