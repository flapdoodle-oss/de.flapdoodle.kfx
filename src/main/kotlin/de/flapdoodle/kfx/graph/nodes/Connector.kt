package de.flapdoodle.kfx.graph.nodes

import de.flapdoodle.kfx.bindings.mapToDouble
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Bounds
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.transform.Rotate

class Connector(val content: Node) : Group() {

    private val angleProperty: DoubleProperty = object : SimpleDoubleProperty(0.0) {
        override fun invalidated() {
            requestLayout()
        }
    }

    fun angle(value: Double) {
        angleProperty.set(value)
    }

    fun angle() = angleProperty.get()

    init {
        children.addAll(content)
        transforms.addAll(Rotate().apply {
            angleProperty().bind(angleProperty)
            val boundsInParentProperty = content.boundsInParentProperty()
            pivotXProperty().bind(boundsInParentProperty.mapToDouble(Bounds::getCenterX))
            pivotYProperty().bind(boundsInParentProperty.mapToDouble(Bounds::getCenterY))
        })
    }
}