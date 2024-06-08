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
package de.flapdoodle.kfx.controls.grapheditor.hints

import de.flapdoodle.kfx.bindings.Values
import de.flapdoodle.kfx.bindings.defaultIfNull
import de.flapdoodle.kfx.extensions.sceneToLocal
import de.flapdoodle.kfx.shapes.Arrow
import de.flapdoodle.kfx.shapes.Curves
import de.flapdoodle.kfx.strokes.LinearGradients
import de.flapdoodle.kfx.types.ColoredAngleAtPoint2D
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Point2D
import javafx.scene.layout.Region
import javafx.scene.paint.Color

class NodeConnectionHint(): Region() {
  private val noopPoint = ColoredAngleAtPoint2D(0.0, 0.0, 0.0, Color.BLACK)
  private val startConnector: SimpleObjectProperty<ColoredAngleAtPoint2D?> = SimpleObjectProperty(noopPoint)
  private val endConnector: SimpleObjectProperty<ColoredAngleAtPoint2D?> = SimpleObjectProperty(noopPoint)

  private val curveStart = startConnector.map { sceneToLocal(it) }.defaultIfNull(Values.constantObject(noopPoint))
  private val curveEnd = endConnector.map { sceneToLocal(it) }.defaultIfNull(Values.constantObject(noopPoint))
  private val startColor = startConnector.map { it?.color }.defaultIfNull(Values.constantObject(Color.BLACK))
  private val endColor = endConnector.map { it?.color }.defaultIfNull(Values.constantObject(Color.BLACK))

  private val curve = Curves.cubicCurve(
    curveStart,
    curveEnd
  )
  private val arrow = Arrow(curveEnd)

  init {
    val colorGradient = LinearGradients.exact(
      curveStart.map { it.point2D },
      curveEnd.map { it.point2D },
      startColor,
      endColor
    )

    children.add(curve.apply {
      strokeWidth = 1.0
      strokeProperty().bind(colorGradient)
      fill = Color.TRANSPARENT
    })
    children.add(arrow.apply {
      strokeWidth = 1.0
      strokeProperty().bind(colorGradient)
      fillProperty().bind(colorGradient)
    })
  }

  fun start(pos: ColoredAngleAtPoint2D) {
    startConnector.value = pos
  }

  fun end(pos: ColoredAngleAtPoint2D) {
    endConnector.value = pos
  }

  fun end(pos: Point2D) {
    endConnector.value = (endConnector.value ?: noopPoint).copy(point2D = pos)
  }
}