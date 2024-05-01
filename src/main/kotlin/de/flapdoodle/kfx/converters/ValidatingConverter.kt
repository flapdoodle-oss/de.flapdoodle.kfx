package de.flapdoodle.kfx.converters

import javafx.util.StringConverter

interface ValidatingConverter<T: Any> {
  fun toString(value: T): String
  fun fromString(value: String): ValueOrError<T>

  companion object {
    fun <T: Any> asStringConverter(
      converter: ValidatingConverter<T>,
      lastExceptionPropertySetter: (Exception?) -> Unit = {}
    ): StringConverter<T> {
      return object : StringConverter<T>() {
        override fun toString(value: T?): String? {
          return value?.let {
            lastExceptionPropertySetter(null)
            converter.toString(it)
          }
        }

        override fun fromString(value: String?): T? {
          return if (value != null && value.trim().isNotEmpty()) {
            when (val v = converter.fromString(value)) {
              is ValueOrError.Value -> {
                lastExceptionPropertySetter(null)
                v.value
              }

              is ValueOrError.Error -> {
                lastExceptionPropertySetter(v.exception)
                null
              }
            }
          } else null
        }
      }
    }

  }
}