/*
 * Copyright (C) 2022
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.kfx.bindings.list

import de.flapdoodle.kfx.collections.IndexedDiff
import de.flapdoodle.kfx.collections.IndexedDiff.between
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.beans.value.WritableValue

class List2ListChangeListener<S, T>(
  private val destination: WritableValue<List<T>>,
  private val transformation: (S) -> T
) : ChangeListener<List<S>> {
  override fun changed(observable: ObservableValue<out List<S>>, oldValue: List<S>, newValue: List<S>) {
    val changes = between(oldValue, newValue)

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