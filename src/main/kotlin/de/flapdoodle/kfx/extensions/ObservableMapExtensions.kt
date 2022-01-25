/**
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

import javafx.collections.ObservableMap
import kotlin.reflect.KClass

object ObservableMapExtensions {
  fun <T: Any> get(map: ObservableMap<Any, Any>, key: Key<T>): T? {
    @Suppress("UNCHECKED_CAST")
    return map[key] as T?
  }

  fun <T: Any> set(map: ObservableMap<Any, Any>, key: Key<T>, value: T?): T? {
    @Suppress("UNCHECKED_CAST")
    return if (value!=null)
      map.put(key,value) as T?
    else
      map.remove(key) as T?
  }

  open class TypedMap(
      private val map: ObservableMap<Any, Any>
  ) {
    open operator fun <T: Any> set(key: Key<T>, value: T?): T? {
      return set(map, key, value)
    }

    operator fun <T: Any> get(key: Key<T>): T? {
      return get(map, key)
    }

    open operator fun <T: Any> set(type: KClass<T>, value: T?): T? {
      return set(map, Key.ofType(type), value)
    }

    operator fun <T: Any> get(type: KClass<T>): T? {
      return get(map, Key.ofType(type))
    }
  }
}