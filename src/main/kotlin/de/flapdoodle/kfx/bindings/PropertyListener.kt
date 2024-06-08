package de.flapdoodle.kfx.bindings

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue

class PropertyListener<T>: ChangeListener<T?> {

  internal val valueProperty = SimpleObjectProperty<T>(null)

  fun onAttach(value: T?) {
    valueProperty.value = value
  }

  override fun changed(observable: ObservableValue<out T>, oldValue: T?, newValue: T?) {
    valueProperty.value = newValue
  }

  fun onDetach() {
    valueProperty.value = null
  }

}