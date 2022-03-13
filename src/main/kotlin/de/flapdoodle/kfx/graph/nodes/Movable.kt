package de.flapdoodle.kfx.graph.nodes

import de.flapdoodle.kfx.extensions.layoutPosition
import de.flapdoodle.kfx.types.LayoutBounds
import javafx.geometry.Dimension2D
import javafx.scene.Node

data class Movable<T: Node>(val node: T, private val size: (T) -> Dimension2D, private val resizeable: ((T) -> Resizeable)? = null) {
    fun size() = size(node)
    fun isResizeable() = (resizeable != null)

    fun rawLayoutBounds() = LayoutBounds(node.layoutPosition, size())

    fun resizeTo(width: Double, height: Double) {
        require(resizeable != null) { "node is not resizable: $node" }
        val asResizeable = resizeable!!(node)
        asResizeable.resizeTo(width, height)
    }
}