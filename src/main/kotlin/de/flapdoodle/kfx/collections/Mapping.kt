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

class Mapping<K, R, V> {
  private var keyMap: Map<K, R> = emptyMap()
  private var reverseMap: Map<R, K> = emptyMap()
  private var map: Map<K, V> = emptyMap()

  fun add(key: K, reverseKey: R, value: V) {
    val oldReverseKey = keyMap[key]
    val oldKey = reverseMap[reverseKey]
    val oldValue = map[key]
    require(oldReverseKey == null) { "reverseKey already set to $oldReverseKey" }
    require(oldKey == null) { "key already set to $oldKey" }
    require(oldValue == null) { "value already set to $oldValue" }

    keyMap = keyMap + (key to reverseKey)
    reverseMap = reverseMap + (reverseKey to key)
    map = map + (key to value)
  }

  fun reverseKey(key: K): R? {
    return keyMap[key]
  }

  operator fun get(key: K): V? {
    return map[key]
  }

  fun with(key: K, onValue: (V) -> Unit) {
    val value = requireNotNull(get(key)) { "could not get value for $key" }
    onValue(value)
  }

  fun key(reverseKey: R): K? {
    return reverseMap[reverseKey]
  }

  fun remove(key: K, onValue: (V) -> Unit = {}) {
    val reverseKey = requireNotNull(keyMap[key]) { "could not find reverse key" }
    val value = requireNotNull(get(key)) { "could not get value for $key" }
    keyMap = keyMap - key
    reverseMap = reverseMap - reverseKey
    map = map - key
    onValue(value)
  }

  fun replace(key: K, value: V, onOldValue: (V) -> Unit = {}) {
    requireNotNull(keyMap[key]) { "could not find reverse key" }
    val oldValue = requireNotNull(get(key)) { "could not get value for $key" }
    map = map - key + (key to value)
    onOldValue(oldValue)
  }
}