package de.flapdoodle.kfx.controls.fields

import de.flapdoodle.kfx.bindings.ObjectBindings
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.value.ObservableValue

interface ValidatingField<T> {
  fun get(): T?
  fun hasError(): Boolean
  fun errorMessage(): String?

  fun lastErrorProperty(): ReadOnlyProperty<String>
  fun valueProperty(): ObjectProperty<T>

  companion object {
    fun invalidInputs(vararg fields: ValidatingField<out Any>): ObservableValue<Boolean> {
      return validInputs(*fields).map { !it }
    }

    fun validInputs(vararg fields: ValidatingField<out Any>): ObjectBinding<Boolean> {
      val anyError = anyError(*fields)
      val anyValueNotSet = anyValueNotSet(*fields)

      return ObjectBindings.merge(anyValueNotSet,anyError) { valueNotSet, error ->
        !error && !valueNotSet
      }
    }

    private fun anyError(vararg fields: ValidatingField<out Any>): ObjectBinding<Boolean> {
      return ObjectBindings.mapAll(fields.map { it.lastErrorProperty() }) { list ->
        list.any { it != null }
      }
    }

    private fun anyValueNotSet(vararg fields: ValidatingField<out Any>): ObjectBinding<Boolean> {
      return ObjectBindings.mapAll(fields.map { it.valueProperty() }) { list ->
        list.any { it == null }
      }
    }
  }
}