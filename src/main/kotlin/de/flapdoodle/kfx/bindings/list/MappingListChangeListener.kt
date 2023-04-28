package de.flapdoodle.kfx.bindings.list

import javafx.collections.ListChangeListener
import javafx.collections.ObservableList

class MappingListChangeListener<S, T>(
  private val destination: ObservableList<T>,
  private val transformation: (S) -> T
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
            destination[index+change.from] = transformation(s)
          }
        }
        change.wasPermutated() -> {
//          println("wasPermutated ${change.from}-${change.to}")
          val list = change.list.subList(change.from, change.to)
//          list.forEach { println("-> $it") }
//          (change.from until change.to).forEach {
//            println("$it -> ${change.getPermutation(it)}")
//          }
          val org = destination.subList(change.from, change.to)
          val copy = ArrayList(org)
          (change.from until change.to).forEach {
            copy[change.getPermutation(it)] = org[it]
          }
          destination.setAll(copy)
        }
        // wasReplaced -> wasAdded && wasRemoved
        change.wasReplaced() -> {
//          println("wasReplaced ${change.from}-${change.to}")
          val replaced = change.list.subList(change.from, change.to)
//          replaced.forEach { println("-> $it") }
          replaced.forEachIndexed { index, s ->
            destination[index+change.from] = transformation(s)
          }
        }
        change.wasAdded() -> {
//          println("wasAdded ${change.from}-${change.to}")
          val added = change.list.subList(change.from, change.to)
//          added.forEach { println("-> $it") }
          added.forEachIndexed { index, s ->
            destination.add(index+change.from, transformation(s))
          }
        }
        change.wasRemoved() -> {
//          println("wasRemoved ${change.from}-${change.from + change.removedSize}")
          val removed = change.removed
//          removed.forEach { println("-> $it") }
          destination.remove(change.from, change.from + change.removedSize)
        }
      }
    }
  }
}