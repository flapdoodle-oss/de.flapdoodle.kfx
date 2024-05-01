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
package de.flapdoodle.kfx.types

import javafx.geometry.Point2D
import javafx.scene.transform.Affine

fun Point2D.to(other: Point2D) = Line2D(this, other)

data class Line2D(val start: Point2D, val end: Point2D) {

    @Deprecated("see Point2DMath")
    fun positionAt(position: UnitInterval, distance: Double, offset: Double): Point2D {

        val diff = end.subtract(start)
        val base = diff.multiply(position.value).add(start)
        var baseAngle = if (isZero(diff))
            0.0
        else
            Point2D(1.0, 0.0).angle(diff)
        
        if (diff.y < 0) {
            baseAngle = 360 - baseAngle
        }

        val rotation = Affine.rotate(baseAngle, 0.0, 0.0)
        val offsetPoint = Point2D(offset, -distance)
        val rotated = rotation.transform(offsetPoint)
        val result = rotated.add(base)
        return result
    }

    private fun isZero(diff: Point2D): Boolean {
        return diff.x == 0.0 && diff.y == 0.0
    }
}