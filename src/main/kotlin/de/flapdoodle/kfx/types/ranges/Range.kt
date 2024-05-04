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
package de.flapdoodle.kfx.types.ranges

interface Range<T> {
    fun offset(value: T, scale: Double): Double
    fun ticks(maxTicks: Int): List<Ticks<T>>

    companion object {
        fun <T : Any> empty(): Range<T> {
            return object : Range<T> {
                override fun offset(value: T, scale: Double): Double {
                    return scale / 2.0
                }

                override fun ticks(maxTicks: Int): List<Ticks<T>> {
                    return emptyList()
                }
            }
        }

        fun <T> single(value: T): Range<T> {
            return object : Range<T> {
                override fun offset(value: T, scale: Double): Double {
                    return scale / 2.0
                }

                override fun ticks(maxTicks: Int): List<Ticks<T>> {
                    return listOf(Ticks(listOf(value)))
                }
            }
        }
    }
}
