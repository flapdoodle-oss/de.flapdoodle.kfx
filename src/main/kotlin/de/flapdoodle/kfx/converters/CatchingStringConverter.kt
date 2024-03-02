package de.flapdoodle.kfx.converters

import javafx.util.StringConverter

class CatchingStringConverter<T>(
  private val delegate: StringConverter<T>,
  private val onFromString: (Exception?) -> Unit
) : StringConverter<T>() {
  override fun toString(value: T?): String {
    return delegate.toString(value)
  }

  override fun fromString(value: String?): T? {
    try {
      val converted = delegate.fromString(value)
      onFromString(null)
      return converted
    } catch (ex: Exception) {
      onFromString(ex)
      return null
    }
  }
}