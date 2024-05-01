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

import de.flapdoodle.kfx.extensions.plus
import javafx.geometry.Point2D
import javafx.scene.transform.Affine

interface AngleAndPoint2D {
  val point2D: Point2D
  val angle: Double

  fun withDistance(distance: Double, deltaAngle: Double = 0.0, offset: Double = 0.0): Point2D {
    return point2D + Affine.rotate(angle + deltaAngle, 0.0, 0.0)
      .transform(Point2D(distance,offset))
  }

  fun atDistance(distance: Double, deltaAngle: Double = 0.0, offset: Double = 0.0): AngleAndPoint2D {
    val newPoint = point2D + Affine.rotate(angle + deltaAngle, 0.0, 0.0)
      .transform(Point2D(distance,offset))
    return AngleAtPoint2D(newPoint, angle + deltaAngle)
  }
}