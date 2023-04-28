package de.flapdoodle.kfx.bindings.list

import javafx.collections.ListChangeListener
import javafx.collections.ObservableList

class IndexedMappingListChangeListener<S, T>(
  private val destination: ObservableList<T>,
  private val transformation: (Int, S) -> T
) : ListChangeListener<S> {
  override fun onChanged(change: ListChangeListener.Change<out S>) {
    while (change.next()) {
//      println("change: $change")
      when {
        change.wasUpdated() -> {
//          println("wasUpdated ${change.from}-${change.to}")
          val updated = change.list.subList(change.from, change.to)
//          updated.forEach { println("-> $it") }
          updated.forEachIndexed { index, s ->
            destination[index+change.from] = transformation(index+change.from, s)
          }
        }
        change.wasPermutated() -> {
//          println("wasPermutated ${change.from}-${change.to}")
          destination.setAll(change.list.mapIndexed(transformation))
        }
        // wasReplaced -> wasAdded && wasRemoved
        change.wasReplaced() -> {
//          println("wasReplaced ${change.from}-${change.to}")
          val replaced = change.list.subList(change.from, change.to)
//          replaced.forEach { println("-> $it") }
          replaced.forEachIndexed { index, s ->
            destination[index+change.from] = transformation(index+change.from, s)
          }
        }
        change.wasAdded() -> {
//          println("wasAdded ${change.from}-${change.to}")
          destination.setAll(change.list.mapIndexed(transformation))
        }
        change.wasRemoved() -> {
//          println("wasRemoved ${change.from}-${change.from + change.removedSize}")
          destination.setAll(change.list.mapIndexed(transformation))
        }
      }
    }
  }
}