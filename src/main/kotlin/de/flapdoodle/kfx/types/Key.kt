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
package de.flapdoodle.kfx.types

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

interface Key<K: Any> {
  fun type(): KClass<K>

  data class ClassKey<K: Any>(val clazz: KClass<K>) : Key<K> {
    override fun toString(): String {
      return "Key(${clazz.simpleName})"
    }

    override fun type(): KClass<K> = clazz
  }

  companion object {
    private val keyIdGeneratorMap = ConcurrentHashMap<Key<out Any>, AtomicInteger>()

    private fun nextIdFor(key: Key<out Any>): Int {
      return keyIdGeneratorMap.getOrPut(key) { AtomicInteger() }.incrementAndGet()
    }

    fun <T: Any> nextId(type:KClass<T>): Int {
      return nextId(keyOf(type))
    }

    fun <T: Any> nextId(key: Key<T>): Int {
      return nextIdFor(key)
    }

    fun <T: Any> keyOf(type: KClass<T>): Key<T> {
      return ClassKey(type)
    }
  }
}