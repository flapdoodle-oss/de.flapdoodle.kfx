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
package de.flapdoodle.kfx.types

data class AutoArray<T : Any> private constructor(
    val map: Map<Int, T> = emptyMap()
) {
    private val maxIndex = map.keys.maxOrNull()

    fun set(index: Int, value: T?): AutoArray<T> {
        return if (value != null)
            copy(map = map + (index to value))
        else
            copy(map = map - index)
    }

    operator fun get(index: Int): T? {
        return map[index]
    }

    fun <D : Any> mapNotNull(function: (T?) -> D?): List<D> {
        return if (maxIndex != null)
            (0..maxIndex).mapNotNull { function(map[it]) }
        else
            emptyList()
    }

    companion object {
        fun <T : Any> empty() = AutoArray<T>()
    }
}