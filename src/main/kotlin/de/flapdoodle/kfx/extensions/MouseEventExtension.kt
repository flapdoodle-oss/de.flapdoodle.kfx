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
package de.flapdoodle.kfx.extensions

import javafx.geometry.Point2D
import javafx.scene.input.MouseEvent

val MouseEvent.screenPosition: Point2D
    get() = Point2D(this.screenX, this.screenY)

val MouseEvent.scenePosition: Point2D
    get() = Point2D(this.sceneX, this.sceneY)

val MouseEvent.localPosition: Point2D
    get() = Point2D(this.x, this.y)

val MouseEvent.isAnyButtonDown: Boolean
    get() = isPrimaryButtonDown || isMiddleButtonDown || isSecondaryButtonDown