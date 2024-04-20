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
import javafx.scene.paint.Color

data class ColoredAngleAtPoint2D(override val point2D: Point2D, override val angle: Double, val color: Color): AngleAndPoint2D {
    constructor(x: Double, y: Double, angle: Double, color: Color): this(Point2D(x,y), angle, color)
}