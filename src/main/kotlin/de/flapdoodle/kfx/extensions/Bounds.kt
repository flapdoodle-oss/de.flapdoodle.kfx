package de.flapdoodle.kfx.extensions

import de.flapdoodle.kfx.bindings.Bindings
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.Parent

fun Parent.childBoundsInParent(): Bounds = de.flapdoodle.kfx.extensions.Bounds.childBoundsInParent(this)
fun Parent.childBoundsInParentPropery(): ReadOnlyObjectProperty<Bounds> = de.flapdoodle.kfx.extensions.Bounds.childBoundsInParentProperty(this)

object Bounds {
    fun childBoundsInParentProperty(parent: Parent): ReadOnlyObjectProperty<Bounds> {
        val childrenAsValue = Bindings.mapList(parent.childrenUnmodifiable) { it }
        val layoutXY = Bindings.mapDouble(parent.layoutXProperty(), parent.layoutYProperty()) { x, y -> Point2D(x,y) }
        val binding = Bindings.map(layoutXY, childrenAsValue) { xy, list -> childBoundsInParent(xy, list) }
        val property = ReadOnlyObjectWrapper<Bounds>()
        property.bind(binding)
        return property.readOnlyProperty
    }

    fun childBoundsInParent(parent: Parent): Bounds {
        return childBoundsInParent(parent.layoutPosition, parent.childrenUnmodifiable)
    }

    private fun childBoundsInParent(layoutXY: Point2D, children: List<Node>): Bounds {
        val bounds = children.map { it.boundsInParent }
        return when (bounds.size) {
            0 -> BoundingBox(layoutXY.x, layoutXY.y, -1.0, -1.0)
            1 -> bounds[0]
            else -> bounds.reduce { a: Bounds, b: Bounds -> merge(a, b) }
        }
    }

    private fun merge(a: Bounds, b: Bounds): Bounds {
        return BoundingBox(
            Math.min(a.minX, b.minX),
            Math.min(a.minY, b.minY),
            Math.min(a.minZ, b.minZ),
            Math.max(a.maxX, b.maxX),
            Math.max(a.maxY, b.maxY),
            Math.max(a.maxZ, b.maxZ)
        )
    }
}