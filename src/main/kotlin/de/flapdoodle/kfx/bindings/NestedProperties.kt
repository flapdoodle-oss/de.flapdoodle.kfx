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
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue

class NestedProperties<C : Any, T : Any>(
  node: ObservableValue<out C?>,
  binding: (C) -> ObjectBinding<T?>
): ObservableValue<T?> {

  private val valueProperty = SimpleObjectProperty<T>(null)

  init {
    node.addListener { _, old, new ->
      if (old != null) {
        valueProperty.unbind()
      }
      if (new !=null) {
        valueProperty.bind(binding(new))
      } else {
        valueProperty.value = null
      }
    }
    node.value?.let { valueProperty.bind(binding(it)) }
  }

  override fun addListener(listener: ChangeListener<in T?>) = valueProperty.addListener(listener)
  override fun removeListener(listener: ChangeListener<in T?>) = valueProperty.removeListener(listener)

  override fun addListener(listener: InvalidationListener?) = valueProperty.addListener(listener)
  override fun removeListener(listener: InvalidationListener?) = valueProperty.removeListener(listener)

  override fun getValue(): T? = valueProperty.value
}