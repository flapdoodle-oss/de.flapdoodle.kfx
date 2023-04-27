package de.flapdoodle.kfx.bindings

import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.ObservableMap

class MapKVListChangeListener<S, K, V>(
  private val destination: ObservableMap<K, V>,
  private val keyOf: (S) -> K,
  private val valueOf: (S) -> V
) : ListChangeListener<S> {
  override fun onChanged(change: ListChangeListener.Change<out S>) {
    while (change.next()) {
//      println("change: $change")
      when {
        change.wasUpdated() -> {
//          println("wasUpdated ${change.from}-${change.to}")
          val updated = change.list.subList(change.from, change.to)
//          updated.forEach { println("-> $it") }
          updated.forEach { s ->
            destination[keyOf(s)] = valueOf(s)
          }
        }

        change.wasPermutated() -> {
          // ignore order changes
        }
        // wasReplaced -> wasAdded && wasRemoved
        change.wasReplaced() -> {
          change.removed.forEach { s ->
            destination.remove(keyOf(s))
          }
          val replaced = change.list.subList(change.from, change.to)
//          replaced.forEach { println("-> $it") }
          replaced.forEach { s ->
            destination[keyOf(s)] = valueOf(s)
          }
        }

        change.wasAdded() -> {
//          println("wasAdded ${change.from}-${change.to}")
          val added = change.list.subList(change.from, change.to)
//          added.forEach { println("-> $it") }
          added.forEach { s ->
            destination[keyOf(s)] = valueOf(s)
          }
        }

        change.wasRemoved() -> {
//          println("wasRemoved ${change.from}-${change.from + change.removedSize}")
          val removed = change.removed
          removed.forEach { s ->
            destination.remove(keyOf(s))
          }
        }
      }
    }
  }
}