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

import kotlin.reflect.KClass

abstract class Key<T: Any> {
  companion object {
    fun <T: Any> ofType(type: KClass<T>): Key<T> {
      return TypeKey(type)
    }

    fun <T: Any> ofType(scope: KClass<out Any>, type: KClass<T>): Key<T> {
      return Scoped(scope, type)
    }

    fun <T: Any> of(value: T): Key<T> {
      return ValueKey(value)
    }
  }

  private data class TypeKey<T: Any>(
      private val type: KClass<T>
  ) : Key<T>()

  private data class Scoped<T: Any>(
      private val scope: KClass<out Any>,
      private val type: KClass<T>
  ) : Key<T>()

  private data class ValueKey<T: Any>(
    private val value: T
  ) : Key<T>()
}