package de.flapdoodle.kfx.shapes

import de.flapdoodle.kfx.bindings.and
import de.flapdoodle.kfx.bindings.mapToDouble
import de.flapdoodle.kfx.types.AngleAndPoint2D
import javafx.beans.value.ObservableValue
import javafx.geometry.Point2D
import javafx.scene.shape.CubicCurve
import javafx.util.Subscription

object Curves {
  fun cubicCurve(
    start: ObservableValue<out AngleAndPoint2D>,
    end: ObservableValue<out AngleAndPoint2D>
  ): CubicCurve {
    return CubicCurve().apply {
      bindControls(start, end)
    }
  }

  fun CubicCurve.bindControls(
    start: ObservableValue<out AngleAndPoint2D>,
    end: ObservableValue<out AngleAndPoint2D>
  ): Subscription {
    val startPointProperty = start.map(AngleAndPoint2D::point2D)
    val endPointProperty = end.map(AngleAndPoint2D::point2D)

    val distance = startPointProperty.and(endPointProperty)
      .map { s, e -> e.distance(s) }
    val startDistance = distance.map { it.div(2) }
    val endDistance = distance.map { it.div(3) }

    startXProperty().bind(startPointProperty.mapToDouble(Point2D::getX))
    startYProperty().bind(startPointProperty.mapToDouble(Point2D::getY))

    val startWithDist = start.and(startDistance).map(AngleAndPoint2D::withDistance)

    controlX1Property().bind(startWithDist.mapToDouble(Point2D::getX))
    controlY1Property().bind(startWithDist.mapToDouble(Point2D::getY))

    val endWithDist = end.and(endDistance).map(AngleAndPoint2D::withDistance)

    controlX2Property().bind(endWithDist.mapToDouble(Point2D::getX))
    controlY2Property().bind(endWithDist.mapToDouble(Point2D::getY))

    endXProperty().bind(endPointProperty.mapToDouble(Point2D::getX))
    endYProperty().bind(endPointProperty.mapToDouble(Point2D::getY))

    return Subscription {
      startXProperty().unbind()
      startYProperty().unbind()

      controlX1Property().unbind()
      controlY1Property().unbind()

      controlX2Property().unbind()
      controlY2Property().unbind()

      endXProperty().unbind()
      endYProperty().unbind()

      endWithDist.dispose()
      startWithDist.dispose()

      distance.dispose()

      //endPointProperty.dispose()
      //startPointProperty.dispose()
    }
  }

//  fun CubicCurve.bindStroke(
//    start: ObservableValue<Color>,
//    end: ObservableValue<Color>
//  ): Registration {
//    val gradient = LinearGradient(
//      0.0,  // start X
//      0.0,  // start Y
//      1.0,  // end X
//      1.0,  // end Y
//      false,  // proportional
//      CycleMethod.NO_CYCLE,  // cycle colors
//      // stops
//      Stop(0.0, Color.rgb(0, 0, 255, 0.0)),
//      Stop(1.0, Color.rgb(0, 0, 255, 1.0))
//    )
//
//    //startXProperty().addListener({ _, _, x -> gradient.startX=x })
//
//    val old = stroke
//
//    return Registration {
//      stroke = old
//    }
//  }
}