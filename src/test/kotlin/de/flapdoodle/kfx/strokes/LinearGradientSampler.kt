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
package de.flapdoodle.kfx.strokes

import de.flapdoodle.kfx.events.SharedLock
import de.flapdoodle.kfx.extensions.scenePosition
import de.flapdoodle.kfx.shapes.Curves
import de.flapdoodle.kfx.types.AngleAtPoint2D
import javafx.application.Application
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Point2D
import javafx.scene.Scene
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import javafx.stage.Stage

class LinearGradientSampler {

  class Sample : Application() {
    override fun start(stage: Stage) {
      val pane = Pane()
      val start = SimpleObjectProperty(AngleAtPoint2D(400.0, 100.0, 90.0))
      val end = SimpleObjectProperty(AngleAtPoint2D(595.0, 100.0, 180.0))

      val acolor = LinearGradient(
        0.0,  // start X
        0.0,  // start Y
        1.0,  // end X
        1.0,  // end Y
        true,  // proportional
        CycleMethod.NO_CYCLE,  // cycle colors
        // stops
        Stop(0.0, Color.rgb(255, 0, 0, 1.0)),
        Stop(1.0, Color.rgb(255, 0, 0, 0.0))
      )

      val a = Curves.cubicCurve(start, end)
      a.fill = Color.TRANSPARENT
      a.stroke = Color.RED
      a.strokeWidth = 5.0
      a.stroke = acolor

      val bcolor = LinearGradient(
        0.0,  // start X
        0.0,  // start Y
        1.0,  // end X
        1.0,  // end Y
        true,  // proportional
        CycleMethod.NO_CYCLE,  // cycle colors
        // stops
        Stop(0.0, Color.rgb(0, 0, 255, 0.0)),
        Stop(1.0, Color.rgb(0, 0, 255, 1.0))
      )

      val b = Curves.cubicCurve(start, end)
      b.fill = Color.TRANSPARENT
      b.stroke = Color.BLUE
      b.strokeWidth = 5.0
      b.stroke = bcolor

      val startColor = SimpleObjectProperty(Color.rgb(255, 0, 0, 1.0))
      val endColor = SimpleObjectProperty(Color.rgb(0, 0, 255, 1.0))

      val startC = start.map { it.copy(point2D = Point2D(it.point2D.x - 50.0, it.point2D.y + 50.0)) }
      val endC = end.map { it.copy(point2D = Point2D(it.point2D.x - 50.0, it.point2D.y + 50.0)) }

      val c = Curves.cubicCurve(startC, endC)
      c.fill = Color.TRANSPARENT
      c.strokeWidth = 5.0
      c.strokeProperty().bind(LinearGradients.cardinal(startC.map(AngleAtPoint2D::point2D), endC.map(AngleAtPoint2D::point2D), startColor, endColor))

      val startD = start.map { it.copy(point2D = Point2D(it.point2D.x - 100.0, it.point2D.y + 100.0)) }
      val endD = end.map { it.copy(point2D = Point2D(it.point2D.x - 100.0, it.point2D.y + 100.0)) }

      val d = Curves.cubicCurve(startD, endD)
      d.fill = Color.TRANSPARENT
      d.strokeWidth = 5.0
      d.strokeProperty().bind(LinearGradients.exact(startD.map(AngleAtPoint2D::point2D), endD.map(AngleAtPoint2D::point2D), startColor, endColor))

      val lock = SharedLock<Pane>()

      pane.children.addAll(a, b, c, d)
      pane.addEventFilter(MouseEvent.MOUSE_PRESSED) { event ->
        lock.tryLock(pane) { event.scenePosition }
      }
      pane.addEventFilter(MouseEvent.MOUSE_RELEASED) { event ->
        lock.tryRelease(pane, Point2D::class.java) {

        }
      }
      pane.addEventFilter(MouseEvent.MOUSE_DRAGGED) { event ->
        lock.ifLocked(pane, Point2D::class.java) { lock ->
//        println("diff: ${event.scenePosition.minus(lock.value)}")
          end.value = end.value.copy(point2D = event.scenePosition)
        }
      }

      stage.scene = Scene(pane, 600.0, 400.0)
      stage.show()
    }
  }

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      Application.launch(Sample::class.java, *args)
    }
  }

}