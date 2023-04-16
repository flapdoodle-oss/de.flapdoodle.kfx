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
package de.flapdoodle.kfx.layout.virtual

import de.flapdoodle.kfx.bindings.mapToDouble
import javafx.beans.value.ObservableValue
import javafx.geometry.Bounds
import javafx.scene.control.ScrollBar

fun ScrollBar.setBounds(scrollBounds: ScrollBounds) {
    this.min = scrollBounds.min
    this.max = scrollBounds.max
    this.visibleAmount = scrollBounds.visibleAmount
}

fun ScrollBar.bind(scrollBounds: ObservableValue<ScrollBounds>) {
    this.minProperty().bind(scrollBounds.mapToDouble { it.min })
    this.maxProperty().bind(scrollBounds.mapToDouble { it.max })
    this.visibleAmountProperty().bind(scrollBounds.mapToDouble { it.visibleAmount })
}

data class ScrollBounds(
    val min: Double,
    val max: Double,
    val visibleAmount: Double
    ) {

    companion object {
        private fun of(windowSize: Number, itemSize: Double, itemOffset: Double, currentItemOffset: Number): ScrollBounds {
            return of(
                windowSize = windowSize.toDouble(),
                itemSize = itemSize,
                itemOffset = itemOffset,
                currentItemOffset = currentItemOffset.toDouble(),
                false
            )
        }

        fun widthOf(width: Number, bounds: Bounds, layoutX: Number): ScrollBounds {
            return of(
                windowSize = width,
                itemSize = bounds.width,
                itemOffset = bounds.minX,
                currentItemOffset = layoutX,
            )
        }

        fun heightOf(height: Number, bounds: Bounds, layoutY: Number): ScrollBounds {
            return of(
                windowSize = height,
                itemSize = bounds.height,
                itemOffset = bounds.minY,
                currentItemOffset = layoutY,
            )
        }

        fun of(
            windowOffset: Double, // +-
            windowSize: Double, // >0
            zoom: Double, // 0<zoom<n
            contentOffset: Double, // +-
            contentSize: Double, // >=0
        ) {
//            println("----------------")
//            println("window: $windowOffset - $windowSize")
//            println("zoom: $zoom")
//            println("content: $contentOffset - $contentSize")
//            println()

            val fact = contentSize / windowSize
            if (fact <= 1) {
                // full visible
                


            } else {
                
            }
        }

        fun of(
            windowSize: Double, // >0
            itemSize: Double, // >=0
            itemOffset: Double, // +-
            currentItemOffset: Double,
            debug: Boolean = false
        ): ScrollBounds {
            val fact = itemSize / windowSize // it < 1 if item is smaller

            if (fact<=1) {
                // full visible
                val diff = windowSize - itemSize
                val max = -itemOffset
                val min = max + diff

                val fixedMax = Math.max(min, currentItemOffset)
                val fixedMin = Math.min(max, currentItemOffset)

                val visibleAmount = diff * fact

                if (debug) {
                    println("--------------------------")
                    println("windowSize: $windowSize")
                    println("itemSize: $itemSize")
                    println("itemOffset: $itemOffset")
                    println("currentItemOffset: $currentItemOffset")
                    println("--------------------------")
                    println("diff: $diff")
                    println("max: $max")
                    println("min: $min")
                    println("fixedMax: $fixedMax")
                    println("fixedMin: $fixedMin")
                    println("visibleAmount: $visibleAmount")
                    println()
                    println()
                }

                return ScrollBounds(fixedMin, fixedMax, visibleAmount)

            } else {
                // partial visible
                val diff = itemSize - windowSize
                val max = -itemOffset
                val min = max - diff

                val fixedMin = Math.min(min, currentItemOffset)
                val fixedMax = Math.max(max, currentItemOffset)

                val visibleAmount = diff / fact

                return ScrollBounds(fixedMin, fixedMax, visibleAmount)
            }
        }
    }
}
