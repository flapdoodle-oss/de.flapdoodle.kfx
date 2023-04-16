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

import javafx.geometry.Dimension2D
import javafx.scene.layout.Region
import javafx.scene.shape.Rectangle

val Region.size: Dimension2D
    get() = Dimension2D(width, height)

val Rectangle.size: Dimension2D
    get() = Dimension2D(width, height)

operator fun Dimension2D.minus(other: Dimension2D): Dimension2D {
    return Dimension2D(this.width - other.width, this.height - other.height)
}

operator fun Dimension2D.plus(other: Dimension2D): Dimension2D {
    return Dimension2D(this.width + other.width, this.height + other.height)
}

fun Dimension2D.isEmpty(): Boolean {
    return width==0.0 && height==0.0
}

fun Dimension2D.addWidth(value: Double): Dimension2D = Dimension2D(this.width + value, this.height)
fun Dimension2D.addHeight(value: Double): Dimension2D = Dimension2D(this.width, this.height + value)
fun Dimension2D.subWidth(value: Double): Dimension2D = addWidth(-value)
fun Dimension2D.subHeight(value: Double): Dimension2D = addHeight(-value)


