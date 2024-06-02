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
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue

class NestedProperty<C : Any, T>(
  node: ObservableValue<out C?>,
  property: (C) -> ObservableValue<T>
): ObservableValue<T?> {

  private val propertyListener = PropertyListener<T>()

  init {
    node.addListener { _, old, new ->
      if (old != null) {
        propertyListener.onDetach()
        property(old).removeListener(propertyListener)
      }
      if (new !=null) {
        property(new).addListener(propertyListener)
        propertyListener.onAttach(property(new).value)
      }
    }
    propertyListener.onAttach(node.value?.let { property(it).value })
    node.value?.let { property(it).addListener(propertyListener) }
  }

  override fun addListener(listener: ChangeListener<in T?>) = propertyListener.valueProperty.addListener(listener)
  override fun removeListener(listener: ChangeListener<in T?>) = propertyListener.valueProperty.removeListener(listener)

  override fun addListener(listener: InvalidationListener?) = propertyListener.valueProperty.addListener(listener)
  override fun removeListener(listener: InvalidationListener?) = propertyListener.valueProperty.removeListener(listener)
  
  override fun getValue(): T? = propertyListener.valueProperty.value

  class PropertyListener<T>: ChangeListener<T?> {

    internal val valueProperty = SimpleObjectProperty<T>(null)

    fun onAttach(value: T?) {
      valueProperty.value = value
    }

    override fun changed(observable: ObservableValue<out T>, oldValue: T?, newValue: T?) {
      valueProperty.value = newValue
    }

    fun onDetach() {
      valueProperty.value = null
    }

  }
}