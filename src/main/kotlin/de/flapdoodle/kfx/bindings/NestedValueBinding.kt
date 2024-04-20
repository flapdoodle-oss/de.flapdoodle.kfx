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

class NestedValueBinding<S, T>(
  private val valueBinding: ObservableValue<S?>,
  private val propertyAccess: (S) -> ObservableValue<T>
) : ObjectBinding<T?>() {

  private val dependencies = FXCollections.observableArrayList(valueBinding)

  private var property: ObservableValue<T>? = null

  private val invalidationFromProperty = InvalidationListener {
    invalidate()
  }

  private val changeListener = ChangeListener<T> { _, _, _ ->
    invalidate()
  }

  init {
    bind(valueBinding)
  }

  override fun dispose() {
    unbind(valueBinding)
    super.dispose()
  }

  override fun onInvalidating() {
    super.onInvalidating()
  }

  override fun getDependencies(): ObservableList<*> {
    return dependencies
  }

  override fun computeValue(): T? {
    val current = valueBinding.value
    if (current!=null) {
      val newProperty = propertyAccess(current)
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

  companion object {
    fun <S, T> of(source: ObservableValue<S?>, propertyAccess: (S) -> ObservableValue<T>): ObservableValue<T?> {
      return NestedValueBinding(source, propertyAccess)
    }
  }
}