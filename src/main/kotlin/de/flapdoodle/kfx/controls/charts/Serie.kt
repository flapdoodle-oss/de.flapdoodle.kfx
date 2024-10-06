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
package de.flapdoodle.kfx.controls.charts

import de.flapdoodle.kfx.types.Id
import javafx.scene.paint.Color

data class Serie<X, Y>(
    val label: String,
    val color: Color,
    val points: List<Point<X, Y>>,
    val lines: List<Line<X, Y>>,
    val id: Id<Serie<*,*>> = Id.nextId(Serie::class)
) {
    data class Point<X, Y>(val x: X, val y: Y)
    data class Line<X, Y>(val points: List<Point<X, Y>>)
}