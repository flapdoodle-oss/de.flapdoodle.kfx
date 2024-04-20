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
package de.flapdoodle.kfx.extensions

import com.sun.javafx.binding.MappedBinding
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue

object ObservableValueExtensions {

  fun <T: Any> addChangeListenerAsLast(delegate: ObservableValue<T>, lastListener: ChangeListener<in T>): ObservableValueWrapper<T> {
    delegate.addListener(lastListener)

    return object : ObservableValueWrapper<T>(delegate) {
      override fun addListener(listener: ChangeListener<in T>) {
        super.removeListener(lastListener)
        super.addListener(listener)
        super.addListener(lastListener)
      }
    }
  }

  fun <T, R> mapNullable(src: ObservableValue<T?>, map: (T?) -> R): ObservableValue<R> {
    return object : MappedBinding<T, R>(src, map) {
      override fun computeValue(): R {
        return map.invoke(src.value)
      }
    }
  }

  open class ObservableValueWrapper<T: Any>(val delegate: ObservableValue<T>) : ObservableValue<T> by delegate {

  }
}