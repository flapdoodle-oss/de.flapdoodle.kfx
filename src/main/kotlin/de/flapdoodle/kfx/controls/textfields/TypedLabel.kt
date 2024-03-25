package de.flapdoodle.kfx.controls.textfields

import de.flapdoodle.kfx.converters.Converters
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ObjectPropertyBase
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Label
import kotlin.reflect.KClass

class TypedLabel<T: Any>(
  val type: KClass<T>
) : Label() {

  private val converter = Converters.converterFor(type)
  private val valueProperty = SimpleObjectProperty<T>(null)

  init {
    textProperty().bindBidirectional(valueProperty, converter)
  }

  fun valueProperty(): ObjectProperty<T> = valueProperty

  fun set(v: T?) {
    valueProperty.value = v
  }
}