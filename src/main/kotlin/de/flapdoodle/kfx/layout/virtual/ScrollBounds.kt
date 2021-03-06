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

import javafx.scene.control.ScrollBar

fun ScrollBar.setBounds(scrollBounds: ScrollBounds) {
    this.min = scrollBounds.min
    this.max = scrollBounds.max
    this.visibleAmount = scrollBounds.visibleAmount
}

data class ScrollBounds(
    val min: Double,
    val max: Double,
    val visibleAmount: Double
    ) {

    companion object {
        fun of(
            windowSize: Double, // >0
            itemSize: Double, // >=0
            itemOffset: Double, // +-
            currentItemOffset: Double
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
