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

import javafx.geometry.Point2D

operator fun Point2D.minus(other: Point2D): Point2D {
    return Point2D(this.x - other.x, this.y - other.y)
}

operator fun Point2D.plus(other: Point2D): Point2D {
    return Point2D(this.x + other.x, this.y + other.y)
}

operator fun Point2D.plus(value: Double): Point2D = this.add(value, value)

fun Point2D.addX(value: Double): Point2D = Point2D(this.x + value, this.y)
fun Point2D.addY(value: Double): Point2D = Point2D(this.x, this.y + value)
fun Point2D.subX(value: Double): Point2D = addX(-value)
fun Point2D.subY(value: Double): Point2D = addY(-value)
