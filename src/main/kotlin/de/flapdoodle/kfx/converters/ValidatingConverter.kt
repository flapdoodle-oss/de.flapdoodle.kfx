package de.flapdoodle.kfx.converters

interface ValidatingConverter<T: Any> {
  fun toString(value: T): String
  fun fromString(value: String): ValueOrError<T>
}