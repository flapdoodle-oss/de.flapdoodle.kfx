package de.flapdoodle.kfx.controls.textfields

import de.flapdoodle.kfx.converters.Converters
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.control.TextField
import kotlin.reflect.KClass

class TypedTextField<T: Any>(
  val type: KClass<T>
) : TextField() {
  private val converter = Converters.converterFor(type)
  private val valueProperty = SimpleObjectProperty<T>(null)

  fun valueProperty(): ReadOnlyProperty<T> = valueProperty

  init {
    onAction = EventHandler {
      try {
        valueProperty.value = converter.fromString(text)
      } catch (ex: Exception) {
        valueProperty.value = null
      }
    }
  }

  fun set(v: T?) {
    valueProperty.value = v
    text = if (v!=null) converter.toString(v) else ""
  }

  fun get(): T? {
    return valueProperty.value
  }
}