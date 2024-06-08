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

import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.MapChangeListener
import javafx.collections.ObservableMap

class MapProperty<K : Any, T>(
  node: ObservableMap<K, ObservableValue<T>>,
  key: K
): ObservableValue<T?> {

  private val propertyListener = PropertyListener<T>()

  init {
    node.addListener( MapChangeListener { change ->
      if (change.key == key) {
        if (change.wasAdded() && change.wasRemoved()) {
          val removed = requireNotNull(change.valueRemoved) { "was removed, but is null: $change"}
          removed.removeListener(propertyListener)

          val added = requireNotNull(change.valueAdded) { "was added, but is null: $change" }
          added.addListener(propertyListener)

          propertyListener.changed(added, change.valueRemoved?.value, added.value)
        } else {
          if (change.wasRemoved()) {
            val removed = requireNotNull(change.valueRemoved) { "was removed, but is null: $change"}
            propertyListener.onDetach()
            removed.removeListener(propertyListener)
          }
          if (change.wasAdded()) {
            val added = requireNotNull(change.valueAdded) { "was added, but is null: $change" }
            added.addListener(propertyListener)
            propertyListener.onAttach(added.value)
          }
        }
      }
    })
    propertyListener.onAttach(node[key]?.value)
    node[key]?.addListener(propertyListener)
  }

  override fun addListener(listener: ChangeListener<in T?>) = propertyListener.valueProperty.addListener(listener)
  override fun removeListener(listener: ChangeListener<in T?>) = propertyListener.valueProperty.removeListener(listener)

  override fun addListener(listener: InvalidationListener?) = propertyListener.valueProperty.addListener(listener)
  override fun removeListener(listener: InvalidationListener?) = propertyListener.valueProperty.removeListener(listener)
  
  override fun getValue(): T? = propertyListener.valueProperty.value
}