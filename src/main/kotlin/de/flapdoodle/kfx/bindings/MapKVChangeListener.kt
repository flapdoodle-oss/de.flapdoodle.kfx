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
package de.flapdoodle.kfx.bindings

import de.flapdoodle.kfx.collections.Diff
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableMap

class MapKVChangeListener<S, K, V>(
  private val destination: ObservableMap<K, V>,
  private val keyOf: (S) -> K,
  private val valueOf: (S) -> V
) : ChangeListener<List<S>> {
  override fun changed(observable: ObservableValue<out List<S>>, oldValue: List<S>, newValue: List<S>) {
    val changes = Diff.between(oldValue, newValue, keyOf)
    changes.removed.forEach {
      destination.remove(keyOf(it))
    }
    changes.modified.forEach { (old, new) ->
      destination[keyOf(old)] = valueOf(new)
    }
    changes.added.forEach {
      destination[keyOf(it)] = valueOf(it)
    }
    
//    while (change.next()) {
////      println("change: $change")
//      when {
//        change.wasUpdated() -> {
////          println("wasUpdated ${change.from}-${change.to}")
//          val updated = change.list.subList(change.from, change.to)
////          updated.forEach { println("-> $it") }
//          updated.forEach { s ->
//            destination[keyOf(s)] = valueOf(s)
//          }
//        }
//
//        change.wasPermutated() -> {
//          // ignore order changes
//        }
//        // wasReplaced -> wasAdded && wasRemoved
//        change.wasReplaced() -> {
//          change.removed.forEach { s ->
//            destination.remove(keyOf(s))
//          }
//          val replaced = change.list.subList(change.from, change.to)
////          replaced.forEach { println("-> $it") }
//          replaced.forEach { s ->
//            destination[keyOf(s)] = valueOf(s)
//          }
//        }
//
//        change.wasAdded() -> {
////          println("wasAdded ${change.from}-${change.to}")
//          val added = change.list.subList(change.from, change.to)
////          added.forEach { println("-> $it") }
//          added.forEach { s ->
//            destination[keyOf(s)] = valueOf(s)
//          }
//        }
//
//        change.wasRemoved() -> {
////          println("wasRemoved ${change.from}-${change.from + change.removedSize}")
//          val removed = change.removed
//          removed.forEach { s ->
//            destination.remove(keyOf(s))
//          }
//        }
//      }
//    }
  }
}