package de.flapdoodle.kfx.converters

import javafx.util.StringConverter

abstract class ReadOnlyStringConverter<T> : StringConverter<T>() {
  final override fun fromString(value: String): T {
    throw IllegalArgumentException("not supported (value = $value)");
  }

  companion object {
    fun <T> with(converter: (T) -> String): ReadOnlyStringConverter<T> {
      return object : ReadOnlyStringConverter<T>() {
        override fun toString(value: T): String {
          return converter(value)
        }
      }
    }
  }
}