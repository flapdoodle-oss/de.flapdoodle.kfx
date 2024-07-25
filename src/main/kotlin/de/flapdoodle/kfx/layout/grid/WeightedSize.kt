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
package de.flapdoodle.kfx.layout.grid

data class WeightedSize(
    val weight: Double,
    val min: Double,
    val max: Double
) {
    init {
        require(weight >= 0.0) { "invalid weight: $weight" }
        require(min >= 0.0) { "invalid min: $min" }
        require(max >= 0.0 && max >= min) { "invalid max: $max (min: $min)" }
    }

    companion object {
        fun distribute(space: Double, items: List<WeightedSize>): List<Double> {
            return if (items.isNotEmpty())
                distributeNonEmpty(space, items)
            else
                emptyList()
        }

        private fun distributeNonEmpty(space: Double, items: List<WeightedSize>): List<Double> {
            val minWidth = items.sumOf { it.min }
            val maxWidth = doubleMaxIfInfinite(items.sumOf { it.max })

            if (minWidth >= space) {
                return items.map { it.min }
            }
            if (maxWidth <= space) {
                return items.map { it.max }
            }

            val sizedItems = items.map(Companion::SizedItems)
            distributeFinal(space, sizedItems)
            return sizedItems.map { it.size() }
        }

        private fun distributeFinal(space: Double, items: List<SizedItems>) {
            var spaceLeft = space

            // filter all items where max < size
            do {
                var repeat=false
                var spaceUsed = 0.0

                val filtered = items.filter { !it.upperLimitReached() }
                val sumOfWeight = filtered.sumOf { it.src.weight }
                filtered.forEach {
                    val size = spaceLeft * it.src.weight / sumOfWeight
                    if (size >= it.src.max) {
                        it.setSize(it.src.max).onUpperLimit()
                        spaceUsed += it.size()
                        repeat = true
                    }
                }
                spaceLeft -= spaceUsed
            } while (repeat)

            // filter all items where min > size
            val itemsWithoutUpperLimits = items.filter { !it.upperLimitReached() }
            do {
                var repeat=false
                var spaceUsed = 0.0

                val filtered = itemsWithoutUpperLimits.filter { !it.lowerLimitReached() }
                val sumOfWeight = filtered.sumOf { it.src.weight }
                filtered.forEach {
                    val size = spaceLeft * it.src.weight / sumOfWeight
                    if (size <= it.src.min) {
                        it.setSize(it.src.min).onLowerLimit()
                        spaceUsed += it.size()
                        repeat = true
                    }
                }
                spaceLeft -= spaceUsed
            } while (repeat)

            // everything what's left should not have any limit problems
            val itemsWithoutLowerLimits = itemsWithoutUpperLimits.filter { !it.lowerLimitReached() }

            val finalSumOfWeights = itemsWithoutLowerLimits.sumOf { it.src.weight }
            itemsWithoutLowerLimits.forEach {
                val size = spaceLeft * it.src.weight / finalSumOfWeights
                it.setSize(size)
            }
        }

        private fun doubleMaxIfInfinite(value: Double): Double {
            return if (value.isInfinite()) Double.MAX_VALUE else value
        }

        private class SizedItems(
            val src: WeightedSize
        ) {
            private var size: Double = 0.0
            private var lowerLimitReached: Boolean = false
            private var upperLimitReached: Boolean = false

            fun lowerLimitReached() = lowerLimitReached
            fun upperLimitReached() = upperLimitReached

            fun size() = size
            fun setSize(size: Double): SizedItems {
                this.size = size
                return this
            }

            fun onLowerLimit() {
                lowerLimitReached=true
            }

            fun onUpperLimit() {
                upperLimitReached=true
            }

            override fun toString(): String {
                return "SizedItem: $src -> lowerLimit: $lowerLimitReached, upperLimit: $upperLimitReached, size=$size"
            }
        }
    }
}