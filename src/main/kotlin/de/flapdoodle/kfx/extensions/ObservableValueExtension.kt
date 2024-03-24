package de.flapdoodle.kfx.extensions

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue

fun <T: Any> ObservableValue<T>.withChangeListenerAlwaysAsLast(lastListener: ChangeListener<in T>): ObservableValue<T> {
  return ObservableValueExtensions.addChangeListenerAsLast(this, lastListener)
}