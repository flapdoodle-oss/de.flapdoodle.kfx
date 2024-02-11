package de.flapdoodle.kfx.strokes

import de.flapdoodle.kfx.graph.nodes.Curves
import de.flapdoodle.kfx.matches
import de.flapdoodle.kfx.types.AngleAtPoint2D
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.CubicCurve
import javafx.stage.Stage
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import org.testfx.framework.junit5.utils.FXUtils

@ExtendWith(ApplicationExtension::class)
class LinearGradientsIT {

  private val startColor = SimpleObjectProperty(Color.rgb(255, 0, 0, 1.0))
  private val endColor = SimpleObjectProperty(Color.rgb(0, 0, 255, 1.0))

  @Start
  private fun createElement(stage: Stage) {
    val width = 400.0
    val height = 400.0

    val center = SimpleObjectProperty(
      AngleAtPoint2D(width/2.0, height/2.0, 0.0)
    )

    val pane = Pane()
    pane.children.addAll(
      curve(center, SimpleObjectProperty(AngleAtPoint2D(400.0, 400.0, 180.0))),
      curve(center, SimpleObjectProperty(AngleAtPoint2D(0.0, 400.0, 0.0))),
      curve(center, SimpleObjectProperty(AngleAtPoint2D(400.0, 0.0, 180.0))),
      curve(center, SimpleObjectProperty(AngleAtPoint2D(0.0, 0.0, 0.0))),
    )
    pane.styleClass.addAll("all")

    stage.scene = Scene(pane, 400.0, 400.0)
    stage.show()
  }

  private fun curve(start: SimpleObjectProperty<AngleAtPoint2D>, destination: SimpleObjectProperty<AngleAtPoint2D>): CubicCurve {
    return Curves.cubicCurve(start, destination).apply {
      fill = Color.TRANSPARENT
      strokeWidth = 3.0
      strokeProperty().bind(LinearGradients.cardinal(start.map { it.point2D }, destination.map { it.point2D }, startColor, endColor))
    }
  }

  @Test
  fun correctStartAndEndColorInAnyDirection(robot: FxRobot) {
    val pane = robot.lookup(".all")
      .queryAs(Pane::class.java)

    robot.capture(pane)
      .matches(javaClass, "gradientInAnyDirection.png")
  }
}

