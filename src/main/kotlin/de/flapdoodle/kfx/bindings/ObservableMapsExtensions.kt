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

import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.util.Subscription

fun <S, K, V> ObservableMap<K, V>.syncWith(source: ObservableValue<List<S>>, keyOf: (S) -> K, valueOf: (S) -> V): Subscription {
  return ObservableMaps.syncWith(source, this, keyOf, valueOf)
}

fun <S, K, V> ObservableMap<K, V>.syncWith(source: ObservableList<S>, keyOf: (S) -> K, valueOf: (S) -> V): Subscription {
  return ObservableMaps.syncWith(source, this, keyOf, valueOf)
}

fun <K, S, T> ObservableMap<K, T>.syncWith(source: ObservableMap<K, S>, transformation: (S) -> T): Subscription {
  return ObservableMaps.syncWith(source, this, transformation)
}

fun <K, V: ObservableValue<T>, T> ObservableMap<K, V>.valueOf(key: K): ObservableValue<T?> {
  return ObservableMaps.valueOf(this, key)
}