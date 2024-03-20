package de.flapdoodle.kfx.bindings

import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.util.Subscription

fun <S, K, V> ObservableMap<K, V>.syncWith(source: ObservableValue<List<S>>, keyOf: (S) -> K, valueOf: (S) -> V): Subscription {
  return ObservableMaps.syncWith(source, this, keyOf, valueOf)
}

fun <S, K, V> ObservableMap<K, V>.syncWith(source: ObservableList<S>, keyOf: (S) -> K, valueOf: (S) -> V): Subscription {
  return ObservableMaps.syncWith(source, this, keyOf, valueOf)
}

fun <K, S, T> ObservableMap<K, T>.syncWith(source: ObservableMap<K, S>, transformation: (S) -> T): Subscription {
  return ObservableMaps.syncWith(source, this, transformation)
}

fun <K, V: ObservableValue<T>, T> ObservableMap<K, V>.valueOf(key: K): ObservableValue<T?> {
  return ObservableMaps.valueOf(this, key)
}