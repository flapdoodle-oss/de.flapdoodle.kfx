package de.flapdoodle.kfx.extensions

import de.flapdoodle.kfx.bindings.LazyProperty
import de.flapdoodle.kfx.bindings.mapToDouble
import javafx.beans.InvalidationListener
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.shape.Rectangle
import java.util.function.Predicate

object BoundingBoxes {
    fun empty() = BoundingBox(0.0, 0.0, 0.0, -1.0, -1.0,-1.0)

    fun merge(a: Bounds, b: Bounds): Bounds {
        if (a.isEmpty) return b
        if (b.isEmpty) return a

        val minX = Math.min(a.minX, b.minX)
        val minY = Math.min(a.minY, b.minY)
        val minZ = Math.min(a.minZ, b.minZ)
        val maxWidth = Math.max(a.maxX, b.maxX) - minX
        val maxHeight = Math.max(a.maxY, b.maxY) - minY
        val maxDepth = Math.max(a.maxZ, b.maxZ) - minZ

        return BoundingBox(
            minX,
            minY,
            minZ,
            maxWidth,
            maxHeight,
            maxDepth
        )
    }

    fun reduce(bounds: List<Bounds>): Bounds {
        return when (bounds.size) {
            0 -> empty()
            1 -> bounds[0]
            else -> bounds.reduce { a, b -> merge(a,b) }
        }
    }

    fun multiply(bounds: Bounds, factor: Double): Bounds {
        return BoundingBox(
            bounds.minX * factor,
            bounds.minY * factor,
            bounds.minZ * factor,
            bounds.width * factor,
            bounds.height * factor,
            bounds.depth * factor
        )
    }

    fun reduceBounds(nodeList: Collection<Node>): Bounds {
        return reduceBounds(nodeList, Node::getBoundsInParent)
    }

    fun boundsInParentProperty(parent: Node, filter: Predicate<Node>): ReadOnlyObjectProperty<Bounds> {
        return parent.property.computeIfAbsend(BoundsInParentProperty::class) {
            BoundsInParentProperty(parent, filter)
        }
    }

    fun <T: Node> reduceBounds(nodeList: Collection<T>, boundsOfNode: (T) -> Bounds): Bounds {
        return reduce(nodeList.map { boundsOfNode(it) })
    }

    fun <T: Node> reduceBoundsProperty(parent: Node, boundMapping: BoundMapping<T>): ReadOnlyObjectProperty<Bounds> {
        return parent.property.computeIfAbsent(Key.of(boundMapping)) {
            BoundsProperty(parent, boundMapping)
        }
    }

    fun around(points: Collection<Point2D>):Bounds {
        if (!points.isEmpty()) {
            val minX = points.minOf { it.x }
            val minY = points.minOf { it.y }
            val maxX = points.maxOf { it.x }
            val maxY = points.maxOf { it.y }
            return BoundingBox(
                minX, minY, 0.0,
                maxX-minX, maxY-minY, -1.0
            )
        }
        return empty()
    }

    private class BoundsInParentProperty(val parent: Node, val filter: Predicate<Node>) : LazyProperty<Bounds>() {
        init {
            parent.boundsInParentProperty().addListener(InvalidationListener {
                invalidate()
            })
            parent.boundsInParentProperty().addListener { _, _, _ ->
                invalidate()
            }
        }

        override fun computeValue(): Bounds {
            return if (parent is Parent) {
                parent.localToParent(reduceBounds(parent.childrenUnmodifiable.filtered(filter)))
            } else empty()
        }

        override fun getBean(): Any {
            return parent
        }

        override fun getName(): String {
            return "BoundsInParentProperty"
        }
    }

    private class BoundsProperty<T: Node>(val parent: Node, val boundMapping: BoundMapping<T>) : LazyProperty<Bounds>() {
        init {
            parent.boundsInParentProperty().addListener(InvalidationListener {
                invalidate()
            })
            parent.boundsInParentProperty().addListener { _, _, _ ->
                invalidate()
            }
        }

        override fun computeValue(): Bounds {
            return if (parent is Parent) {
                parent.localToParent(reduceBounds(boundMapping.childOfNode(parent), boundMapping.boundsOfChild))
            } else empty()
        }

        override fun getBean(): Any {
            return parent
        }

        override fun getName(): String {
            return "BoundsInParentProperty"
        }
    }

    fun bindRectangle(wrapperBounds: ObservableValue<Bounds>): Rectangle {
        val rect = Rectangle().apply {
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

  data class BoundMapping<T: Node>(
        val childOfNode: (Node) -> Collection<T>,
        val boundsOfChild: (T) -> Bounds
    )
}