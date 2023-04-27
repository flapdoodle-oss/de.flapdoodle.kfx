package de.flapdoodle.kfx.bindings

import javafx.collections.ListChangeListener

class AddOrRemoveListChangeListener<T>(
  val onAdded: (T) -> Unit,
  val onRemoved: (T) -> Unit
) : ListChangeListener<T> {
  override fun onChanged(change: ListChangeListener.Change<out T>) {
    while (change.next()) {
      when {
        change.wasAdded() -> {
          change.list.subList(change.from, change.to)
            .forEach(onAdded)
        }

        change.wasRemoved() -> {
          change.removed.forEach(onRemoved)
        }
      }
    }
  }
}