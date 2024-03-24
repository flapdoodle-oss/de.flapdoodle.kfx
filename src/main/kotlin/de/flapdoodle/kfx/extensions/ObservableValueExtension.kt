package de.flapdoodle.kfx.extensions

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.util.Subscription

fun <T> ObservableValue<T>.addChangeListener(changeListener: ChangeListener<T>): Subscription {
  addListener(changeListener)
  return Subscription {
    removeListener(changeListener)
  }
}