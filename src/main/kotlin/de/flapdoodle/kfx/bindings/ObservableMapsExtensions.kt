package de.flapdoodle.kfx.bindings

import de.flapdoodle.kfx.Registration
import javafx.collections.ObservableList
import javafx.collections.ObservableMap

fun <S, K, V> ObservableMap<K, V>.syncWith(source: ObservableList<S>, keyOf: (S) -> K, valueOf: (S) -> V): Registration {
  return ObservableMaps.syncWith(source, this, keyOf, valueOf)
}