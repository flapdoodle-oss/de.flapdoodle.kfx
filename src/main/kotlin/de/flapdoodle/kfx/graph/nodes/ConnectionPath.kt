package de.flapdoodle.kfx.graph.nodes

import de.flapdoodle.kfx.bindings.and
import de.flapdoodle.kfx.bindings.map
import de.flapdoodle.kfx.bindings.mapToDouble
import de.flapdoodle.kfx.types.AngleAtPoint2D
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.paint.Color
import javafx.scene.shape.CubicCurve

class ConnectionPath(val start: Connector, val end: Connector) : Group() {
    private val path = CubicCurve().apply {
        strokeWidth = 1.0
        stroke = Color.BLACK
        fill = Color.TRANSPARENT

        val startProperty = start.connectionPointProperty()
        val startPointProperty = startProperty.map(AngleAtPoint2D::point2D)

        val endProperty = end.connectionPointProperty()
        val endPointProperty = endProperty.map(AngleAtPoint2D::point2D)

        val distance = startPointProperty.and(endPointProperty)
            .map { s, e -> e.distance(s).div(3)}

        startXProperty().bind(startPointProperty.mapToDouble(Point2D::getX))
        startYProperty().bind(startPointProperty.mapToDouble(Point2D::getY))

        val startWithDist = startProperty.and(distance).map(AngleAtPoint2D::withDistance)

        controlX1Property().bind(startWithDist.mapToDouble(Point2D::getX))
        controlY1Property().bind(startWithDist.mapToDouble(Point2D::getY))

        val endWithDist = endProperty.and(distance).map(AngleAtPoint2D::withDistance)

        controlX2Property().bind(endWithDist.mapToDouble(Point2D::getX))
        controlY2Property().bind(endWithDist.mapToDouble(Point2D::getY))

        endXProperty().bind(endPointProperty.mapToDouble(Point2D::getX))
        endYProperty().bind(endPointProperty.mapToDouble(Point2D::getY))
    }

    init {
        children.addAll(path)
    }
}