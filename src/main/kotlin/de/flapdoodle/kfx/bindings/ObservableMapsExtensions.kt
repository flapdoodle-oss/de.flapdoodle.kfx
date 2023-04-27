package de.flapdoodle.kfx.bindings

import de.flapdoodle.kfx.Registration
import javafx.collections.ObservableList
import javafx.collections.ObservableMap

fun <S, K, V> ObservableMap<K, V>.syncWith(source: ObservableList<S>, keyOf: (S) -> K, valueOf: (S) -> V): Registration {
  return ObservableMaps.syncWith(source, this, keyOf, valueOf)
}

fun <K, S, T> ObservableMap<K, T>.syncWith(source: ObservableMap<K, S>, transformation: (S) -> T): Registration {
  return ObservableMaps.syncWith(source, this, transformation)
}