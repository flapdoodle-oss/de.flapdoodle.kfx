package de.flapdoodle.kfx.bindings.list

import de.flapdoodle.kfx.collections.IndexedDiff
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList

class List2ObservableListChangeListener<S, T>(
  private val destination: ObservableList<T>,
  private val transformation: (S) -> T
) : ChangeListener<List<S>> {
  override fun changed(observable: ObservableValue<out List<S>>, oldValue: List<S>, newValue: List<S>) {
    val changes = IndexedDiff.changes(oldValue, newValue)

    val removeItems = changes.filterIsInstance<IndexedDiff.Change.Remove<S>>()
    val addItems = changes.filterIsInstance<IndexedDiff.Change.Add<S>>()
    val moves = changes.filterIsInstance<IndexedDiff.Change.Move<S>>()

    if (moves.isNotEmpty()) {
      val copy = ArrayList<T>()
      removeItems.reversed().forEach {
        destination.remove(it.index, it.index + 1)
      }
      changes.forEach {
        when(it) {
          is IndexedDiff.Change.Move<S> -> copy.add(destination[it.source])
          is IndexedDiff.Change.Add<S> -> copy.add(transformation(it.value))
          else -> { }
        }
       }
      destination.setAll(copy)
    } else {
      removeItems.reversed().forEach {
        destination.remove(it.index, it.index + 1)
      }

      addItems.forEach {
        destination.add(it.index, transformation(it.value))
      }
    }
  }
}