package de.flapdoodle.kfx.bindings

import javafx.collections.MapChangeListener
import javafx.collections.ObservableMap

class MappingMapChangeListener<K, S, T>(
  private val destination: ObservableMap<K, T>,
  private val transformation: (S) -> T
) : MapChangeListener<K, S> {
  override fun onChanged(change: MapChangeListener.Change<out K, out S>) {
    if (change.wasRemoved()) {
      destination.remove(change.key)
    }
    if (change.wasAdded()) {
      destination.put(change.key, transformation(change.valueAdded))
    }
  }
}