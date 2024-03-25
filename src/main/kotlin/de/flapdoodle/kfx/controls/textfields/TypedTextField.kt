package de.flapdoodle.kfx.controls.textfields

import de.flapdoodle.kfx.converters.CatchingStringConverter
import de.flapdoodle.kfx.converters.Converters
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.TextField
import kotlin.reflect.KClass

class TypedTextField<T: Any>(
  val type: KClass<T>
) : TextField() {

  private val valueProperty = SimpleObjectProperty<T>(null)
  private val lastExceptionProperty = SimpleObjectProperty<Exception>(null)
  private val converter = CatchingStringConverter(Converters.converterFor(type), onFromString = {
    lastExceptionProperty.value = it
  })

  fun valueProperty(): ReadOnlyProperty<T> = valueProperty
  fun lastExceptionProperty(): ReadOnlyProperty<Exception> = lastExceptionProperty

  init {
    textProperty().bindBidirectional(valueProperty, converter)
  }

  fun set(v: T?) {
    valueProperty.value = v
  }

  fun get(): T? {
    return valueProperty.value
  }
}