package de.flapdoodle.kfx.bindings.list

import de.flapdoodle.kfx.collections.IndexedDiff
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.beans.value.WritableValue
import javafx.collections.ObservableList

class List2ListChangeListener<S, T>(
  private val destination: WritableValue<List<T>>,
  private val transformation: (S) -> T
) : ChangeListener<List<S>> {
  override fun changed(observable: ObservableValue<out List<S>>, oldValue: List<S>, newValue: List<S>) {
    val changes = IndexedDiff.changes(oldValue, newValue)

    val removeItems = changes.filterIsInstance<IndexedDiff.Change.Remove<S>>()
    val addItems = changes.filterIsInstance<IndexedDiff.Change.Add<S>>()
    val moves = changes.filterIsInstance<IndexedDiff.Change.Move<S>>()

    val realMoves = moves.count { it.source != it.destination } != 0
    var destCopy = destination.value

    if (realMoves) {
      var copy = emptyList<T>()
      // TODO ist das nicht sinnlos?
      removeItems.reversed().forEach {
        destCopy = remove(destCopy, it.index, it.index + 1)
      }
      changes.forEach {
        when (it) {
          is IndexedDiff.Change.Move<S> -> copy = copy + destCopy[it.source]
          is IndexedDiff.Change.Add<S> -> copy = copy + transformation(it.value)
          else -> {}
        }
      }
      destCopy = copy
    } else {
      removeItems.reversed().forEach {
        destCopy = remove(destCopy, it.index, it.index + 1)
      }

      addItems.forEach {
        destCopy = add(destCopy, it.index, transformation(it.value))
      }
    }

    destination.value = destCopy
  }

  private fun add(src: List<T>, index: Int, value: T): List<T> {
    return src.subList(0, index) + value + src.subList(index, src.size)
  }

  private fun remove(src: List<T>, start: Int, end: Int): List<T> {
    return src.subList(0,start) + src.subList(end, src.size)
  }
}