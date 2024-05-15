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
package de.flapdoodle.kfx.converters

sealed class ValueOrError<T: Any> {
  abstract fun <M: Any> mapValue(mapper: (T) -> M): ValueOrError<M>
  fun <M: Any> flatMap(mapper: (T) -> ValueOrError<M>): ValueOrError<M> {
    return when (this) {
      is Value<T> -> mapper(this.value)
      is Error<T> -> Error(exception)
    }
  }

  data class Value<T: Any>(val value: T): ValueOrError<T>() {
    override fun <M : Any> mapValue(mapper: (T) -> M): ValueOrError<M> {
      try {
        return Value(mapper(value))
      } catch (ex: Exception) {
        return Error(ex)
      }
    }
  }
  
  data class Error<T: Any>(val exception: Exception): ValueOrError<T>() {
    override fun <M : Any> mapValue(mapper: (T) -> M): ValueOrError<M> {
      return Error(exception)
    }
  }
}