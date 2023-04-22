package de.flapdoodle.kfx.graph.nodes

import de.flapdoodle.kfx.bindings.and
import de.flapdoodle.kfx.bindings.map
import de.flapdoodle.kfx.bindings.mapToDouble
import de.flapdoodle.kfx.types.AngleAtPoint2D
import javafx.beans.value.ObservableValue
import javafx.geometry.Point2D
import javafx.scene.shape.CubicCurve

object Curves {
    fun cubicCurve(
        start: ObservableValue<AngleAtPoint2D>,
        end:  ObservableValue<AngleAtPoint2D>
    ): CubicCurve {
        return CubicCurve().apply {
            bindControls(start, end)
        }
    }

    fun CubicCurve.bindControls(
        start: ObservableValue<AngleAtPoint2D>,
        end: ObservableValue<AngleAtPoint2D>
    ) {
        val startPointProperty = start.map(AngleAtPoint2D::point2D)
        val endPointProperty = end.map(AngleAtPoint2D::point2D)

        val distance = startPointProperty.and(endPointProperty)
            .map { s, e -> e.distance(s).div(2) }

        startXProperty().bind(startPointProperty.mapToDouble(Point2D::getX))
        startYProperty().bind(startPointProperty.mapToDouble(Point2D::getY))

        val startWithDist = start.and(distance).map(AngleAtPoint2D::withDistance)

        controlX1Property().bind(startWithDist.mapToDouble(Point2D::getX))
        controlY1Property().bind(startWithDist.mapToDouble(Point2D::getY))

        val endWithDist = end.and(distance).map(AngleAtPoint2D::withDistance)

        controlX2Property().bind(endWithDist.mapToDouble(Point2D::getX))
        controlY2Property().bind(endWithDist.mapToDouble(Point2D::getY))

        endXProperty().bind(endPointProperty.mapToDouble(Point2D::getX))
        endYProperty().bind(endPointProperty.mapToDouble(Point2D::getY))
    }
}