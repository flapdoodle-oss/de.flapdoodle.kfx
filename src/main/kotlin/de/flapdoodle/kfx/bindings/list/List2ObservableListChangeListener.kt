package de.flapdoodle.kfx.bindings.list

import de.flapdoodle.kfx.collections.IndexedDiff
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList

class List2ObservableListChangeListener<S, T>(
  private val destination: ObservableList<T>,
  private val transformation: (S) -> T
): ChangeListener<List<S>> {
  override fun changed(observable: ObservableValue<out List<S>>, oldValue: List<S>, newValue: List<S>) {
    val changes = IndexedDiff.changes(oldValue, newValue)
  }
}