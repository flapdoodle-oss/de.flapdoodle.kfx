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
package de.flapdoodle.kfx.layout.virtual

import de.flapdoodle.kfx.bindings.DoubleBindings
import de.flapdoodle.kfx.bindings.and
import de.flapdoodle.kfx.bindings.mapToDouble
import de.flapdoodle.kfx.types.AngleAtPoint2D
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.Bounds
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import javafx.scene.transform.Rotate
import javafx.scene.transform.Transform

class Connector(val content: Node = Circle(10.0, Color.GREY)) : Group() {

    private val angleProperty: DoubleProperty = object : SimpleDoubleProperty(0.0) {
        override fun invalidated() {
            requestLayout()
        }
    }

    private val connectionProperty: ReadOnlyObjectWrapper<AngleAtPoint2D> = ReadOnlyObjectWrapper()

    init {
        children.addAll(content)

        val line = Line().apply {
            strokeWidth = 1.0
            stroke = Color.BLACK
            val boundsInParentProperty = content.boundsInLocalProperty()
            startXProperty().bind(boundsInParentProperty.mapToDouble(Bounds::getCenterX))
            startYProperty().bind(boundsInParentProperty.mapToDouble(Bounds::getCenterY))
            endXProperty().bind(boundsInParentProperty.mapToDouble(Bounds::getMaxX))
            endYProperty().bind(boundsInParentProperty.mapToDouble(Bounds::getCenterY))
        }
        children.addAll(line)
        transforms.addAll(Rotate().apply {
            angleProperty().bind(angleProperty)
            val boundsInParentProperty = content.boundsInParentProperty()
            pivotXProperty().bind(boundsInParentProperty.mapToDouble(Bounds::getCenterX))
            pivotYProperty().bind(boundsInParentProperty.mapToDouble(Bounds::getCenterY))
        })

//        addEventHandler(MouseEvent.MOUSE_CLICKED) {
//            angle(angle()+10.0)
//            it.consume()
//        }

        val endPoint: ObservableValue<Point2D> = DoubleBindings.merge(line.endXProperty(), line.endYProperty(), ::Point2D)
        val endPointInParent = localToParentTransformProperty().and(endPoint).map(Transform::transform)
        val pointWithAngle = endPointInParent.and(angleProperty).map { a, b -> AngleAtPoint2D(a, b.toDouble()) }
        connectionProperty.bind(pointWithAngle)
    }

    fun angle(value: Double) {
        angleProperty.set(value)
    }
    fun angle() = angleProperty.get()
    fun angleProperty() = angleProperty

    fun connectionPoint() = connectionProperty.get()
    fun connectionPointProperty() = connectionProperty.readOnlyProperty
}