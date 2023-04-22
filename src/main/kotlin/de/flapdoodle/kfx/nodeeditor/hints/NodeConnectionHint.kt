package de.flapdoodle.kfx.nodeeditor.hints

import de.flapdoodle.kfx.bindings.Values
import de.flapdoodle.kfx.bindings.defaultIfNull
import de.flapdoodle.kfx.bindings.map
import de.flapdoodle.kfx.extensions.sceneToLocal
import de.flapdoodle.kfx.graph.nodes.Curves
import de.flapdoodle.kfx.types.AngleAtPoint2D
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Point2D
import javafx.scene.layout.Region
import javafx.scene.paint.Color

class NodeConnectionHint(): Region() {
  private val noopPoint = AngleAtPoint2D(0.0, 0.0, 0.0)
  private val startConnector: SimpleObjectProperty<AngleAtPoint2D?> = SimpleObjectProperty(noopPoint)
  private val endConnector: SimpleObjectProperty<AngleAtPoint2D?> = SimpleObjectProperty(noopPoint)

  private val curve = Curves.cubicCurve(
    startConnector.map { sceneToLocal(it) }.defaultIfNull(Values.constantObject(noopPoint)),
    endConnector.map { sceneToLocal(it) }.defaultIfNull(Values.constantObject(noopPoint))
  )

  init {
    children.add(curve.apply {
      strokeWidth = 1.0
      stroke = Color.BLUE
      fill = Color.TRANSPARENT
    })
  }

  fun start(pos: AngleAtPoint2D) {
    startConnector.value = pos
  }

  fun end(pos: AngleAtPoint2D) {
    endConnector.value = pos
  }

  fun end(pos: Point2D) {
    endConnector.value = (endConnector.value ?: noopPoint).copy(point2D = pos)
  }
}