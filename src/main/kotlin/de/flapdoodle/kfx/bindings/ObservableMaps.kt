package de.flapdoodle.kfx.bindings

import de.flapdoodle.kfx.Registration
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.collections.WeakListChangeListener

object ObservableMaps {
  fun <S, K, V> syncWith(source: ObservableList<S>, destination: ObservableMap<K, V>, keyOf: (S) -> K, valueOf: (S) -> V): Registration {
    source.forEach {
      destination[keyOf(it)] = valueOf(it)
    }
    val listener = WeakListChangeListener(MapKVListChangeListener(destination, keyOf, valueOf))
    source.addListener(listener)

    return Registration {
      source.removeListener(listener)
    }
  }
}