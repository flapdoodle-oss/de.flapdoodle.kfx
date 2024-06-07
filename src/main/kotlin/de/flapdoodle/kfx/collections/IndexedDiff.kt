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

object IndexedDiff {
  @Deprecated("use between")
  fun <T> changes(old: List<T>, new: List<T>): List<Change<T>> {
    return between(old, new)
  }

  /**
   * first removed, then move and add
   */
  fun <T> between(old: List<T>, new: List<T>): List<Change<T>> {
    var changes = emptyList<Change<T>>()

    val oldAsSet = old.toSet()
    val newAsSet = new.toSet()
    val removed = oldAsSet - newAsSet

    changes = changes + old.flatMapIndexed { index: Int, t: T -> if (removed.contains(t)) listOf(Change.Remove(index)) else emptyList() }
    changes = changes + moveAndAdd(old.filter { !removed.contains(it) }, new)

    return changes
  }

  private fun <T> moveAndAdd(old: List<T>, new: List<T>): List<Change<T>> {
    var oldIndexMap = old.mapIndexed { index: Int, t: T ->
      t to index
    }.groupBy(Pair<T, Int>::first) { it.second }

    val moveAndAdd: List<Change<T>> = new.flatMapIndexed { index: Int, it: T ->
      val oldIndexList = oldIndexMap[it]
      if (oldIndexList != null && oldIndexList.isNotEmpty()) {
        val firstIndex = oldIndexList[0]
        oldIndexMap = oldIndexMap - it + (it to oldIndexList.subList(1, oldIndexList.size))
//        if (firstIndex != index) {
          listOf(Change.Move(firstIndex, index))
//        } else emptyList()
      } else {
        listOf(Change.Add(index, it))
      }
    }

    val removeOld = if (old.size > new.size) {
      List(old.size - new.size) { index -> Change.Remove(index + new.size) }
    } else
      emptyList<Change<T>>()

    return moveAndAdd + removeOld
  }

  sealed class Change<T> {
    data class Remove<T>(val index: Int) : Change<T>()
    data class Move<T>(val source: Int, val destination: Int) : Change<T>()
    data class Add<T>(val index: Int, val value: T) : Change<T>()
  }
}