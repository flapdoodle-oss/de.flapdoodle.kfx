package de.flapdoodle.kfx.types

import de.flapdoodle.kfx.extensions.plus
import javafx.geometry.Point2D
import javafx.scene.transform.Affine

interface AngleAndPoint2D {
  val point2D: Point2D
  val angle: Double

  fun withDistance(distance: Double): Point2D {
    return point2D + Affine.rotate(angle, 0.0, 0.0)
      .transform(Point2D(distance,0.0))
  }

}