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
import javafx.beans.value.WritableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.util.Subscription

fun <S, T> ObservableList<T>.syncWith(source: ObservableList<S>, mapping: (S) -> T): Subscription {
  return ObservableLists.syncWith(source, this, mapping)
}

fun <S, T> ObservableList<T>.syncWith(source: ObservableValue<List<S>>, mapping: (S) -> T): Subscription {
  return ObservableLists.syncWith(source, this, mapping)
}

fun <S, T> WritableValue<List<T>>.syncWith(source: ObservableValue<List<S>>, mapping: (S) -> T): Subscription {
  return ObservableLists.syncWith(source, this, mapping)
}

fun <T> List<T>.toObservable(): ObservableList<T> = FXCollections.observableList(toMutableList())