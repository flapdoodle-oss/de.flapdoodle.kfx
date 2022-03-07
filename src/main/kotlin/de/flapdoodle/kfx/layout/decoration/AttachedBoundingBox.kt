package de.flapdoodle.kfx.layout.decoration

import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.geometry.Bounds
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.shape.Rectangle

class AttachedBoundingBox(
    node: Node,
    rectangle: Rectangle
) : Group() {

    private val changeListener = ChangeListener<Bounds> { _, _, bounds ->
        resize(rectangle, bounds)
    }

    private fun resize(rectangle: Rectangle, bounds: Bounds) {
        rectangle.layoutX = bounds.minX
        rectangle.layoutY = bounds.minY
        rectangle.width = bounds.width
        rectangle.height = bounds.height
    }

    init {
        node.boundsInParentProperty().addListener(changeListener)
        node.boundsInParentProperty().addListener(InvalidationListener {
            resize(rectangle, node.boundsInParent)
        })
    }

    companion object {
        fun attach(node: Node, rectangle: Rectangle): AttachedBoundingBox {
            return AttachedBoundingBox(node, rectangle)
        }
    }
}