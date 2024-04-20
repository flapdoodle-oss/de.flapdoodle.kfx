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
          destination.remove(change.from, change.from + change.removedSize)

          val added = change.list.subList(change.from, change.to)
          added.forEachIndexed { index, s ->
            destination.add(index+change.from, transformation(index+change.from, s))
          }
////          println("wasReplaced ${change.from}-${change.to}")
//          val replaced = change.list.subList(change.from, change.to)
////          replaced.forEach { println("-> $it") }
//          replaced.forEachIndexed { index, s ->
//            destination[index+change.from] = transformation(index+change.from, s)
//          }
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