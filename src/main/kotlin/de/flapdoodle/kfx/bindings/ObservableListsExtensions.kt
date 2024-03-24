package de.flapdoodle.kfx.bindings

import javafx.beans.property.ReadOnlyProperty
import javafx.beans.value.ObservableValue
import javafx.beans.value.WritableValue
import javafx.collections.ObservableList
import javafx.util.Subscription

fun <S, T> ObservableList<T>.syncWith(source: ObservableList<S>, mapping: (S) -> T): Subscription {
  return ObservableLists.syncWith(source, this, mapping)
}

fun <S, T> ObservableList<T>.syncWith(source: ObservableValue<List<S>>, mapping: (S) -> T): Subscription {
  return ObservableLists.syncWith(source, this, mapping)
}

fun <S, T> WritableValue<List<T>>.syncWith(source: ObservableValue<List<S>>, mapping: (S) -> T): Subscription {
  return ObservableLists.syncWith(source, this, mapping)
}
