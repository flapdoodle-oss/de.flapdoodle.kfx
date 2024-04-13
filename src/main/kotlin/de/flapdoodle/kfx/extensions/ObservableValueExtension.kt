package de.flapdoodle.kfx.extensions

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue

fun <T: Any> ObservableValue<T>.withChangeListenerAlwaysAsLast(lastListener: ChangeListener<in T>): ObservableValue<T> {
  return ObservableValueExtensions.addChangeListenerAsLast(this, lastListener)
}

fun <T: Any, R: Any> ObservableValue<T?>.mapNullable(map: (T?) -> R): ObservableValue<R> {
  return ObservableValueExtensions.mapNullable(this, map)
}