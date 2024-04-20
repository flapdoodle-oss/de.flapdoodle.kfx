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

object Diff {
  fun <T> between(old: Collection<T>, new: Collection<T>): Change<T> {
    return between(old, new) { it }
  }

  fun <K, T> between(old: Collection<T>, new: Collection<T>, keyOf: (T) -> K): Change<T> {
    val oldByKey = old.associateBy(keyOf)
    val newByKey = new.associateBy(keyOf)

    val removed = oldByKey.keys - newByKey.keys
    val added = newByKey.keys - oldByKey.keys
    val stillThere = oldByKey.keys.intersect(newByKey.keys)

    val keyAnPair = stillThere.map { it to (oldByKey[it]!! to newByKey[it]!!) }
    val (notChanged, modified) = keyAnPair.partition { it.second.first == it.second.second }

    return Change(
      removed = removed.map { oldByKey[it]!! }.toSet(),
      notChanged = notChanged.map { it.second.first }.toSet(),
      modified = modified.map { it.second }.toSet(),
      added = added.map { newByKey[it]!! }.toSet()
    )
  }
}