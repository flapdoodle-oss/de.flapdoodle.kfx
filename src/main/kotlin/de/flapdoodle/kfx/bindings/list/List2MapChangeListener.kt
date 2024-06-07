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

import de.flapdoodle.kfx.collections.IndexedDiff.between
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.beans.value.WritableValue

class List2MapChangeListener<S, K, V>(
  private val destination: WritableValue<Map<K, V>>,
  private val key: (S) -> K,
  private val transformation: (S) -> V
) : ChangeListener<List<S>> {
  override fun changed(observable: ObservableValue<out List<S>>, oldValue: List<S>, newValue: List<S>) {
    val changes = between(oldValue, newValue)

//    val removeItems = changes.filterIsInstance<IndexedDiff.Change.Remove<S>>()
//    val addItems = changes.filterIsInstance<IndexedDiff.Change.Add<S>>()
//    val moves = changes.filterIsInstance<IndexedDiff.Change.Move<S>>()
//
//    val realMoves = moves.count { it.source != it.destination } != 0
//
//    if (realMoves) {
//      val copy = ArrayList<V>()
//      removeItems.reversed().forEach {
//        destination.value = destination.value - it.
//      }
//      changes.forEach {
//        when (it) {
//          is IndexedDiff.Change.Move<S> -> copy.add(destination[it.source])
//          is IndexedDiff.Change.Add<S> -> copy.add(transformation(it.value))
//          else -> {}
//        }
//      }
//      destination.setAll(copy)
//    } else {
//      removeItems.reversed().forEach {
//        destination.remove(it.index, it.index + 1)
//      }
//
//      addItems.forEach {
//        destination.add(it.index, transformation(it.value))
//      }
//    }
  }
}