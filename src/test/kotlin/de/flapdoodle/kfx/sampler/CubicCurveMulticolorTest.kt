package de.flapdoodle.kfx.sampler

import de.flapdoodle.kfx.bindings.map
import de.flapdoodle.kfx.events.SharedLock
import de.flapdoodle.kfx.extensions.scenePosition
import de.flapdoodle.kfx.graph.nodes.Curves
import de.flapdoodle.kfx.strokes.LinearGradients
import de.flapdoodle.kfx.types.AngleAtPoint2D
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
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start

@ExtendWith(ApplicationExtension::class)
class CubicCurveMulticolorTest {

  @Start
  private fun createElement(stage: Stage) {
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
    a.strokeWidth = 3.0
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
    b.strokeWidth = 3.0
    b.stroke = bcolor

    val startColor = SimpleObjectProperty(Color.rgb(255, 0, 0, 1.0))
    val endColor = SimpleObjectProperty(Color.rgb(0, 0, 255, 1.0))

    val startC = start.map { it.copy(point2D = Point2D(it.point2D.x - 50.0, it.point2D.y + 50.0)) }
    val endC = end.map { it.copy(point2D = Point2D(it.point2D.x - 50.0, it.point2D.y + 50.0)) }

    val c = Curves.cubicCurve(startC, endC)
    c.fill = Color.TRANSPARENT
    c.strokeWidth = 3.0
    c.strokeProperty().bind(LinearGradients.cardinal(startC.map(AngleAtPoint2D::point2D), endC.map(AngleAtPoint2D::point2D), startColor, endColor))

    val startD = start.map { it.copy(point2D = Point2D(it.point2D.x - 100.0, it.point2D.y + 100.0)) }
    val endD = end.map { it.copy(point2D = Point2D(it.point2D.x - 100.0, it.point2D.y + 100.0)) }

    val d = Curves.cubicCurve(startD, endD)
    d.fill = Color.TRANSPARENT
    d.strokeWidth = 3.0
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

  @Test
  @Disabled
  fun waitSomeTime(robot: FxRobot) {
    println("running for one minute...")
    Thread.sleep(2 * 60 * 1000)
  }

}