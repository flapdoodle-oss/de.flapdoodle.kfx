package de.flapdoodle.kfx.strokes

import de.flapdoodle.kfx.matches
import de.flapdoodle.kfx.shapes.Curves
import de.flapdoodle.kfx.types.AngleAtPoint2D
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Scene
import javafx.scene.layout.Background
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.CubicCurve
import javafx.stage.Stage
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start

@ExtendWith(ApplicationExtension::class)
class LinearGradientsIT {

  private val startColor = SimpleObjectProperty(Color.rgb(255, 0, 0, 1.0))
  private val endColor = SimpleObjectProperty(Color.rgb(0, 0, 255, 1.0))

  @Start
  private fun createElement(stage: Stage) {
    val pane = Pane()
    pane.background = Background.fill(Color.WHITE)
    pane.styleClass.addAll("all")

    stage.scene = Scene(pane, 400.0, 400.0)
    stage.show()
  }

  private fun curve(start: SimpleObjectProperty<AngleAtPoint2D>, destination: SimpleObjectProperty<AngleAtPoint2D>): CubicCurve {
    return Curves.cubicCurve(start, destination).apply {
      fill = Color.TRANSPARENT
      strokeWidth = 2.0
      strokeProperty().bind(LinearGradients.cardinal(start.map { it.point2D }, destination.map { it.point2D }, startColor, endColor))
    }
  }

  @Test
  fun correctStartAndEndColorInAnyDirection(robot: FxRobot) {
    val pane = robot.lookup(".all")
      .queryAs(Pane::class.java)

    robot.interact {
      val width = pane.width
      val height = pane.height

      val center = SimpleObjectProperty(
        AngleAtPoint2D(width/2.0, height/2.0, 45.0)
      )
      pane.children.addAll(
        curve(center, SimpleObjectProperty(AngleAtPoint2D(0.0, 0.0, 0.0))),
        curve(center, SimpleObjectProperty(AngleAtPoint2D(width, 0.0, 180.0))),
        curve(center, SimpleObjectProperty(AngleAtPoint2D(width, height, 180.0))),
        curve(center, SimpleObjectProperty(AngleAtPoint2D(0.0, height, 0.0))),

        curve(center, SimpleObjectProperty(AngleAtPoint2D(width/2.0, 0.0, 90.0))),
        curve(center, SimpleObjectProperty(AngleAtPoint2D(width, height/2.0, 180.0))),
        curve(center, SimpleObjectProperty(AngleAtPoint2D(width/2.0, height, -90.0))),
        curve(center, SimpleObjectProperty(AngleAtPoint2D(0.0, height/2.0, 0.0))),
      )
    }

    robot.capture(pane)
      .matches(javaClass, "gradientInAnyDirection.png")
  }
}

