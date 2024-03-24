package de.flapdoodle.kfx.extensions

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue

object ObservableValueExtensions {

  fun <T: Any> addChangeListenerAsLast(delegate: ObservableValue<T>, lastListener: ChangeListener<in T>): ObservableValueWrapper<T> {
    delegate.addListener(lastListener)

    return object : ObservableValueWrapper<T>(delegate) {
      override fun addListener(listener: ChangeListener<in T>) {
        super.removeListener(lastListener)
        super.addListener(listener)
        super.addListener(lastListener)
      }
    }
  }

  open class ObservableValueWrapper<T: Any>(val delegate: ObservableValue<T>) : ObservableValue<T> by delegate {

  }
}