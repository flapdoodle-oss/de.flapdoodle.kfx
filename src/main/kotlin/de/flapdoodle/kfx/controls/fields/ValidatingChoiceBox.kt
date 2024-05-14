package de.flapdoodle.kfx.controls.fields

import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.ChoiceBox
import javafx.util.StringConverter

class ValidatingChoiceBox<T : Any>(
  val values: List<T?>,
  val default: T?,
  val initialConverter: StringConverter<T>,
  val validate: (T?) -> String?
) : ChoiceBox<T>(), ValidatingField<T> {

  private val lastError = SimpleObjectProperty<String>(null)

  init {
    require(default == null || values.contains(default)) { "default value $default is not in selection: $values" }

    items.addAll(values)
    value = default
    converter = initialConverter

    valueProperty().addListener { observable, oldValue, newValue ->
      lastError.value = validate(newValue)
    }
  }

  override fun get(): T? {
    return value
  }

  override fun hasError(): Boolean {
    return lastError.value != null
  }

  override fun errorMessage(): String? {
    return lastError.value
  }

  override fun lastErrorProperty(): ReadOnlyProperty<String> {
    return lastError
  }

  override fun valueProperty(): ObjectProperty<T> {
    return super.valueProperty()
  }
}