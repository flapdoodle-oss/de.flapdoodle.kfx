package de.flapdoodle.kfx.dialogs

import javafx.beans.value.ObservableValue

interface WizardContent<T: Any> {
  fun isValidProperty(): ObservableValue<Boolean>
  fun result(): T
  fun abort(): T? = null
}