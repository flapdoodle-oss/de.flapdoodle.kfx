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

//            val sumOfWeights = items.sumOf { it.weight }

//            distribute(space, sumOfWeights, sizedItems)

            if (false) {
                val sizedItems = items.map(Companion::SizedItems)
                distribute(space, sizedItems)
                return sizedItems.map { it.size() }
            } else {
                val sizedItems = items.map(Companion::SizedItems)
                distributeFinal(space, sizedItems)
                return sizedItems.map { it.size() }
//            distribute2(space, sizedItems)
//            distribute3(space, sizedItems)
//                return distributeX(space, items)
            }

        }

//        private class ItemWrapper(
//            val src: WeightedSize,
//        ) {
//            var size: Double = 0.0
//
//            fun setSize(newSize: Double) {
//                size = newSize
//            }
//        }
//
//        private fun distributeX(space: Double, items: List<WeightedSize>): List<Double> {
//            val wrapped = items.map { ItemWrapper(it) }
//            dist(space,wrapped)
//            return wrapped.map { it.size }
//        }
//
//        private fun dist(space: Double, items: List<ItemWrapper>) {
//            // TODO
//        }

        private fun distributeFinal(space: Double, items: List<SizedItems>) {
            var repeat = true
            var spaceLeft = space
            var spaceUsed = 0.0

            // filter all items where max < size
            while (repeat) {
                repeat=false

                val filtered = items.filter { !it.upperLimitReached() }
                val sumOfWeight = filtered.sumOf { it.src.weight }
                filtered.forEach {
                    val size = spaceLeft * it.src.weight / sumOfWeight
                    if (size >= it.src.max) {
                        it.setSize(it.src.max).onUpperLimit()
                        spaceUsed = spaceUsed + it.size()
                        repeat = true
                    }
                }
                spaceLeft = spaceLeft - spaceUsed
                spaceUsed = 0.0
            }

            // filter all items where min > size
            val itemsWithoutUpperLimits = items.filter { !it.upperLimitReached() }
            repeat = true
            while (repeat) {
                repeat=false

                val filtered = itemsWithoutUpperLimits.filter { !it.lowerLimitReached() }
                val sumOfWeight = filtered.sumOf { it.src.weight }
                filtered.forEach {
                    val size = spaceLeft * it.src.weight / sumOfWeight
                    if (size <= it.src.min) {
                        it.setSize(it.src.min).onLowerLimit()
                        spaceUsed = spaceUsed + it.size()
                        repeat = true
                    }
                }
                spaceLeft = spaceLeft - spaceUsed
                spaceUsed = 0.0
            }

            // everything what's left should not have any limit problems
            val itemsWithoutLowerLimits = itemsWithoutUpperLimits.filter { !it.lowerLimitReached() }

            val finalSumOfWeights = itemsWithoutLowerLimits.sumOf { it.src.weight }
            itemsWithoutLowerLimits.forEach {
                val size = spaceLeft * it.src.weight / finalSumOfWeights
                it.setSize(size)
            }
        }

        private fun distribute4(space: Double, items: List<SizedItems>) {
            val sorted = items.sortedByDescending { it.src.weight }

            var sizeCalculated = setOf<SizedItems>()

            // 24:24(4), 58:*(1), 24:24(1)
            // was ohne limits size > max hat, kann nicht weiter wachsen
            val initSumOfWeights = sorted.sumOf { it.src.weight }
            sorted.forEach {
//                val size = space * it.src.weight / initSumOfWeights
//                if (size >= it.src.max) {
//                    it.setSize(it.src.max)
//                    sizeCalculated = sizeCalculated + it
//                }
//                if (size <= it.src.min) {
//                    it.setSize(it.src.min)
//                    sizeCalculated = sizeCalculated + it
//                }
            }

            var repeat = true
            while (repeat) {
                repeat = false
                val filtered = sorted.filter { !sizeCalculated.contains(it) }
                val spaceLeft = space - sorted.filter { sizeCalculated.contains(it) }.sumOf { it.size() }

                val sumOfWeights = filtered.sumOf { it.src.weight }
                for (it in filtered) {
                    val size = spaceLeft * it.src.weight / sumOfWeights
                    when {
                        size <= it.src.min -> {
                            it.setSize(it.src.min).clearAnyLimit().onLowerLimit()
                            repeat = true
//                            sizeCalculated = sizeCalculated + it
//                            break
                        }
                        size >= it.src.max -> {
                            it.setSize(it.src.max).clearAnyLimit().onUpperLimit()
                            repeat = true
                            sizeCalculated = sizeCalculated + it
                            break
                        }
                        else -> {
                            it.setSize(size)
                        }
                    }
                }
            }
        }

        private fun distribute(space: Double, items: List<SizedItems>) {
            var spaceUsed = 0.0
            val sumOfWeights = items.sumOf { it.src.weight }

            items.forEach {
                val spaceNeeded = space * it.src.weight / sumOfWeights
                when {
                    spaceNeeded <= it.src.min -> {
                        // do nothing
                    }
                    spaceNeeded >= it.src.max -> {
                        it.setSize(it.src.max).onUpperLimit()
                        spaceUsed = spaceUsed + it.size()
                    }
                    else -> it.setSize(spaceNeeded)
                }
            }

            val growableItems = items.filter { !it.upperLimitReached() }
            val spaceLeft = space - spaceUsed
            val stillSomethingToDo = growableItems.isNotEmpty()
            if (stillSomethingToDo && spaceLeft > 0.0) {

                val leftWeights = growableItems.sumOf { it.src.weight }
                var spaceUsedInGrow = 0.0
                growableItems.forEach {
                    val spaceNeeded = spaceLeft * it.src.weight / leftWeights
                    when {
                        spaceNeeded <= it.src.min -> {
                            it.setSize(it.src.min).onLowerLimit()
                            spaceUsedInGrow = spaceUsedInGrow + it.size()
                        }

                        spaceNeeded >= it.src.max -> {
                            it.setSize(it.src.max).onUpperLimit()
                            spaceUsedInGrow = spaceUsedInGrow + it.size()
                        }

                        else -> it.setSize(spaceNeeded)
                    }
                }
                val stillLeftItems = growableItems.filter { !it.anyLimitReached() }
                val spaceLeftAfterGrow = spaceLeft - spaceUsedInGrow
                val redo = stillLeftItems.isNotEmpty() && stillLeftItems.size != growableItems.size
                if (redo && spaceLeftAfterGrow > 0.0) {
                    distribute(spaceLeftAfterGrow, stillLeftItems)
                }
            }
        }

        private fun distribute(space: Double, sumOfWeights: Double, sizedItems: List<SizedItems>) {
//      println("->>------------------")
//      println("items")
//      sizedItems.forEach { println(it) }

//            val itemsWithLimitsReached = sizedItems.count { it.upperLimitReached() }
//      println("itemsWithLimitsReached: $itemsWithLimitsReached")
            var spaceUsed = 0.0

            sizedItems.forEach {
                val spaceNeeded = space * it.src.weight / sumOfWeights
                when {
                    spaceNeeded <= it.src.min -> it.setSize(it.src.min).onLowerLimit()
                    spaceNeeded >= it.src.max -> it.setSize(it.src.max).onUpperLimit()
                    else -> it.setSize(spaceNeeded)
                }
                if (it.anyLimitReached()) {
                    spaceUsed = spaceUsed + it.size()
                }
            }

//            val newItemsWithLimitsReached = sizedItems.count { it.upperLimitReached() }
//      println("newItemsWithLimitsReached: $newItemsWithLimitsReached")

//            val anyLimitReached = itemsWithLimitsReached != newItemsWithLimitsReached

            val stillSizeableItems = sizedItems.filter { !it.anyLimitReached() }
            val anyNewLimitReached = stillSizeableItems.size != sizedItems.size

            if (anyNewLimitReached) {
                //val spaceUsed = sizedItems.sumByDouble { if (it.limitReached()) it.size() else 0.0 }
//        println("spaceUsed:  $spaceUsed")
                val spaceLeft = space - spaceUsed
//        println("spaceLeft:  $spaceLeft")
                if (spaceLeft > 0.0) {
//          println("again:  spaceLeft=$spaceLeft")
                    val leftSumOfWeights = stillSizeableItems.sumOf { it.src.weight }
                    distribute(spaceLeft, leftSumOfWeights, stillSizeableItems)
                } else {
//          println("finished:  spaceLeft=$spaceLeft")
//          println("items")
//          sizedItems.forEach { println(it) }
                }
            } else {
//        println("finished:  no new limit reached")
//        println("items")
//        sizedItems.forEach { println(it) }
            }
//      println("-<<------------------")
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

            fun allLimitsReached() = lowerLimitReached && upperLimitReached
            fun anyLimitReached() = lowerLimitReached || upperLimitReached
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

            fun clearAnyLimit(): SizedItems {
                lowerLimitReached = false
                upperLimitReached = false
                return this
            }

            override fun toString(): String {
                return "SizedItem: $src -> lowerLimit: $lowerLimitReached, upperLimit: $upperLimitReached, size=$size"
            }
        }
    }
}