package de.flapdoodle.kfx.layout.decoration

import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

object Nodes {
    fun attach(base: Node, attachment: Node, position: Position, attachmentPosition: Position): AttachedNode {
        return AttachedNode(base, attachment, position, attachmentPosition)
    }

    fun attachBoundingBox(base: Node, rectangle: Rectangle): AttachedBoundingBox {
        return AttachedBoundingBox(base, rectangle)
    }

    fun boundingBox(): Rectangle {
        return Rectangle(10.0, 10.0, Color.rgb(255,255, 255, 0.3)).apply {
            strokeWidth = 1.0
            stroke = Color.RED
            strokeDashArray.addAll(5.0, 5.0)
            isMouseTransparent = true
        }
    }
}