package de.flapdoodle.kfx.nodeeditor.hints

import de.flapdoodle.kfx.bindings.Values
import de.flapdoodle.kfx.bindings.defaultIfNull
import de.flapdoodle.kfx.bindings.map
import de.flapdoodle.kfx.extensions.sceneToLocal
import de.flapdoodle.kfx.graph.nodes.Curves
import de.flapdoodle.kfx.strokes.LinearGradients
import de.flapdoodle.kfx.types.AngleAtPoint2D
import de.flapdoodle.kfx.types.ColoredAngleAtPoint2D
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Point2D
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.paint.LinearGradient

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

  init {
    children.add(curve.apply {
      strokeWidth = 1.0
      strokeProperty().bind(LinearGradients.exact(
        curveStart.map { it.point2D },
        curveEnd.map { it.point2D },
        startColor,
        endColor
      ))
      fill = Color.TRANSPARENT
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