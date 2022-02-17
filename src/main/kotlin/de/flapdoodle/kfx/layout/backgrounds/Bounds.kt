package de.flapdoodle.kfx.layout.backgrounds

import de.flapdoodle.kfx.bindings.mapToDouble
import javafx.beans.InvalidationListener
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.geometry.Bounds
import javafx.scene.Node
import javafx.scene.layout.Region
import javafx.scene.shape.Rectangle

object Bounds {
    fun boundsRectangle(node: Node): Rectangle {
        val wrapperBounds: ReadOnlyObjectProperty<Bounds> = node.boundsInParentProperty()

        wrapperBounds.addListener(InvalidationListener {
            node.parent?.requestLayout()
        })

        return Rectangle().apply {
//            styleClass.addAll("content-background")
            isManaged = false
            isMouseTransparent = true

            xProperty().bind(wrapperBounds.mapToDouble(Bounds::getMinX))
            yProperty().bind(wrapperBounds.mapToDouble(Bounds::getMinY))
            widthProperty().bind(wrapperBounds.mapToDouble(Bounds::getWidth))
            heightProperty().bind(wrapperBounds.mapToDouble(Bounds::getHeight))
        }
    }

    fun sizeRectangle(region: Region): Rectangle = Rectangle().apply {
        widthProperty().bind(region.widthProperty())
        heightProperty().bind(region.heightProperty())
    }
}