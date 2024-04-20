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

import javafx.collections.MapChangeListener
import javafx.collections.ObservableMap

class MappingMapChangeListener<K, S, T>(
  private val destination: ObservableMap<K, T>,
  private val transformation: (S) -> T
) : MapChangeListener<K, S> {
  override fun onChanged(change: MapChangeListener.Change<out K, out S>) {
    if (change.wasRemoved()) {
      destination.remove(change.key)
    }
    if (change.wasAdded()) {
      destination.put(change.key, transformation(change.valueAdded))
    }
  }
}