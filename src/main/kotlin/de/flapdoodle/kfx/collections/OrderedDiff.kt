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
package de.flapdoodle.kfx.collections


object OrderedDiff {

//  /**
//   * first removed, then move and add
//   */
//  fun <K, T> between(old: List<T>, new: List<T>, keyOf: (T) -> K): List<Change<T>> {
//    var changes = emptyList<Change<T>>()
//
//    val oldWithKey = old.map { keyOf(it) to it }
//    val newWithKey = new.map { keyOf(it) to it }
//
//    val oldMap = oldWithKey.toMap()
//    require(oldMap.size == oldWithKey.size) { "key collision in $oldWithKey" }
//    val newMap = newWithKey.toMap()
//    require(newMap.size == newWithKey.size) { "key collision in $newWithKey" }
//
//    val removed = oldMap.keys - newMap.keys
//
//    changes = changes + oldWithKey.flatMapIndexed { index: Int, (key: K, t: T) ->
//      if (removed.contains(key)) listOf(Change.Remove(index)) else emptyList()
//    }
//
//    val oldByKey: Map<K, Pair<Int, T>> = oldWithKey.filter { !removed.contains(it.first) }.mapIndexed { index, (key, t) ->
//      key to (index to t)
//    }.associateBy(Pair<K, Pair<Int, T>>::first) { it.second }
//
//    changes = changes + newWithKey.flatMapIndexed { index: Int, (key: K, t: T) ->
//      val oldIndexAndValue = oldByKey[key]
//      if (oldIndexAndValue != null) {
//        val (oldIndex, oldValue) = oldIndexAndValue
//        if (oldValue != t) {
//          listOf(Change.Modify(oldIndex, index, t))
//        } else {
//          if (oldIndex!=index) {
//            listOf(Change.Move(oldIndex, index))
//          } else {
//            emptyList()
//          }
//        }
//      } else {
//        listOf(Change.Add(index, t))
//      }
//    }
//
//
//    return changes
//  }
//
//  sealed class Change<T> {
//    data class Remove<T>(val index: Int) : Change<T>()
//    data class Move<T>(val source: Int, val destination: Int) : Change<T>() {
//      init {
//        require(source!=destination) {"source == destination"}
//      }
//    }
//    data class Modify<T>(val source: Int, val destination: Int, val value: T) : Change<T>()
//    data class Add<T>(val index: Int, val value: T) : Change<T>()
//  }

  fun <K, T> between(old: Collection<T>, new: Collection<T>, keyOf: (T) -> K): Change<T> {
//    var changes = emptyList<OrderedDiff.Change<T>>()

    val oldWithKey = old.map { keyOf(it) to it }
    val newWithKey = new.map { keyOf(it) to it }

    val oldMap = oldWithKey.toMap()
    require(oldMap.size == oldWithKey.size) { "key collision in $oldWithKey" }
    val newMap = newWithKey.toMap()
    require(newMap.size == newWithKey.size) { "key collision in $newWithKey" }

    val removedKeys = oldMap.keys - newMap.keys

    var removed = oldWithKey.flatMap { (key: K, t: T) ->
      if (removedKeys.contains(key)) listOf(t) else emptyList()
    }.toSet()

    val oldByKey: Map<K, Pair<Int, T>> = oldWithKey.filter { !removedKeys.contains(it.first) }.mapIndexed { index, (key, t) ->
      key to (index to t)
    }.associateBy(Pair<K, Pair<Int, T>>::first) { it.second }

    var notChanged = emptySet<T>()
    var modified = emptySet<Pair<T, T>>()
    var added = emptySet<Pair<T, T?>>()

    newWithKey.forEachIndexed { index: Int, (key: K, t: T) ->
      val oldIndexAndValue = oldByKey[key]
      if (oldIndexAndValue != null) {
        val (oldIndex, oldValue) = oldIndexAndValue
        if (oldIndex == index) {
          // same position
          if (oldValue == t) {
            notChanged += t
          } else {
            modified = modified + (oldValue to t)
          }
        } else {
          // different position
//          removed = removed + oldValue
          added = added + (t to oldValue)
        }

      } else {
        added = added + (t to null)
      }
    }

    return Change(
      removed = removed,
      notChanged = notChanged,
      modified = modified,
      added = added
    )
  }

  data class Change<T>(
    val removed: Set<T>,
    val notChanged: Set<T>,
    val modified: Set<Pair<T,T>>,
    val added: Set<Pair<T, T?>>
  )
}