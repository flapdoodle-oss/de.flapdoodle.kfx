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

import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import javafx.geometry.Insets

fun Bounds.multiply(factor: Double): Bounds {
  return BoundingBoxes.multiply(this, factor)
}

operator fun Bounds.minus(insets: Insets?): Bounds {
  if (insets != null) {
    return BoundingBox(
      minX + insets.left,
      minY + insets.top,
      width - insets.right - insets.left,
      height - insets.top - insets.bottom
    )
  }
  return this
}
