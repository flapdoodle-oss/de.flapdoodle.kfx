package de.flapdoodle.kfx.layout.backgrounds

import de.flapdoodle.kfx.bindings.mapToDouble
import de.flapdoodle.kfx.extensions.containerlessBoundsInParentProperty
import javafx.beans.InvalidationListener
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyProperty
import javafx.geometry.Bounds
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.Region
import javafx.scene.shape.Rectangle

object Bounds {
    // TODO does it work?
    fun childBoundsRectangle(parent: Parent): Rectangle {
        val wrapperBounds: ReadOnlyProperty<Bounds> = parent.containerlessBoundsInParentProperty()

        val rect = Rectangle().apply {
//            styleClass.addAll("content-background")
            isManaged = false
            isMouseTransparent = true

            xProperty().bind(wrapperBounds.mapToDouble(Bounds::getMinX))
            yProperty().bind(wrapperBounds.mapToDouble(Bounds::getMinY))
            widthProperty().bind(wrapperBounds.mapToDouble(Bounds::getWidth))
            heightProperty().bind(wrapperBounds.mapToDouble(Bounds::getHeight))
        }

        wrapperBounds.addListener(InvalidationListener {
            rect.parent?.requestLayout()
        })

        return rect
    }

    fun boundsRectangle(node: Node): Rectangle {
        val wrapperBounds: ReadOnlyObjectProperty<Bounds> = node.boundsInParentProperty()

        val rect = Rectangle().apply {
//            styleClass.addAll("content-background")
            isManaged = false
            isMouseTransparent = true

            xProperty().bind(wrapperBounds.mapToDouble(Bounds::getMinX))
            yProperty().bind(wrapperBounds.mapToDouble(Bounds::getMinY))
            widthProperty().bind(wrapperBounds.mapToDouble(Bounds::getWidth))
            heightProperty().bind(wrapperBounds.mapToDouble(Bounds::getHeight))
        }

        wrapperBounds.addListener(InvalidationListener {
            rect.parent?.requestLayout()
        })

        return rect
    }

    fun sizeRectangle(region: Region): Rectangle = Rectangle().apply {
        widthProperty().bind(region.widthProperty())
        heightProperty().bind(region.heightProperty())
    }
}