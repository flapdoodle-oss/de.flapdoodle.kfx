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
import javafx.beans.binding.ObjectBinding
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.util.Subscription

object ObservableMaps {
  fun <S, K, V> syncWith(source: ObservableValue<List<S>>, destination: ObservableMap<K, V>, keyOf: (S) -> K, valueOf: (S) -> V): Subscription {
    source.value.forEach {
      destination[keyOf(it)] = valueOf(it)
    }
    val listener = MapKVChangeListener(destination, keyOf, valueOf)
    source.addListener(listener)

    return Subscription {
      source.removeListener(listener)
    }
  }

  fun <S, K, V> syncWith(source: ObservableList<S>, destination: ObservableMap<K, V>, keyOf: (S) -> K, valueOf: (S) -> V): Subscription {
    source.forEach {
      destination[keyOf(it)] = valueOf(it)
    }
    val listener = MapKVListChangeListener(destination, keyOf, valueOf)
    source.addListener(listener)

    return Subscription {
      source.removeListener(listener)
    }
  }

  fun <K, S, T> syncWith(source: ObservableMap<K, S>, destination: ObservableMap<K, T>, transformation: (S) -> T): Subscription {
    source.forEach { (key, value) ->
      destination[key] = transformation(value)
    }
    val listener = MappingMapChangeListener(destination, transformation)
    source.addListener(listener)

    return Subscription {
      source.removeListener(listener)
    }
  }

  fun <K, V : ObservableValue<T>, T> valueOf(source: ObservableMap<K, V>, key: K): ObservableValue<T?> {
    return ValueOf(source, key)
  }

  internal class ValueOf<K, V : ObservableValue<T>, T>(
    private val map: ObservableMap<K, V>,
    private val key: K
  ) : ObjectBinding<T?>() {

    private val dependencies = FXCollections.observableArrayList(map)
    private var property: ObservableValue<T>? = null

    private val invalidationFromProperty = InvalidationListener {
      invalidate()
    }

    private val changeListener = ChangeListener<T> { _, _, _ ->
      invalidate()
    }

    init {
      bind(map)
    }

    override fun dispose() {
      unbind(map)
      super.dispose()
    }

    override fun onInvalidating() {
      super.onInvalidating()
    }

    override fun getDependencies(): ObservableList<*> {
      return dependencies
    }

    override fun computeValue(): T? {
      val newProperty = map[key]
      if (newProperty != null) {
        if (newProperty != property) {
          property?.removeListener(invalidationFromProperty)
          property?.removeListener(changeListener)

          newProperty.addListener(invalidationFromProperty)
          newProperty.addListener(changeListener)

          property = newProperty
        }
      } else {
        property?.removeListener(invalidationFromProperty)
        property?.removeListener(changeListener)
        property = null
      }

      return property?.value
    }
  }
}