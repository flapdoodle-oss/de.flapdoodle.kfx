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
package de.flapdoodle.kfx.layout.virtual

import de.flapdoodle.kfx.shapes.Curves
import javafx.scene.Group
import javafx.scene.paint.Color

@Deprecated("dont use")
class ConnectionPath(
    val start: Connector,
    val end: Connector
) : Group() {
    private val path = Curves.cubicCurve(
        start.connectionPointProperty(),
        end.connectionPointProperty()
    ).apply {
        strokeWidth = 1.0
        stroke = Color.BLACK
        fill = Color.TRANSPARENT
    }

    init {
        children.addAll(path)
    }
}