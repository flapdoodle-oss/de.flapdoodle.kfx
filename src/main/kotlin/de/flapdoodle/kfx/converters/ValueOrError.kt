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
  fun <M: Any> map(mapper: (T) -> M?): ValueOrError<M> {
    return when (this) {
      is Value<T> -> try {
        return Value(value?.let{ mapper(it) })
      } catch (ex: Exception) {
        return Error(ex)
      }
      is Error<T> -> Error(exception)
    }
  }

  fun <M: Any> mapNullable(mapper: (T?) -> M?): ValueOrError<M> {
    return when (this) {
      is Value<T> -> try {
        return Value(mapper(value))
      } catch (ex: Exception) {
        return Error(ex)
      }
      is Error<T> -> Error(exception)
    }
  }

  data class Value<T: Any>(val value: T?): ValueOrError<T>()
  data class Error<T: Any>(val exception: Exception): ValueOrError<T>()

  companion object {
    fun <T: Any> noValue() = Value<T>(null)
    fun <T: Any> error(exception: Exception) = Error<T>(exception)
  }
}