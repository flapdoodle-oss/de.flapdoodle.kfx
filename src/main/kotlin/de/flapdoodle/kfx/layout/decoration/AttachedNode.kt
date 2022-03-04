package de.flapdoodle.kfx.layout.decoration

import de.flapdoodle.kfx.types.Direction
import de.flapdoodle.kfx.types.Line2D
import de.flapdoodle.kfx.types.Percent
import de.flapdoodle.kfx.types.to
import javafx.beans.value.ChangeListener
import javafx.geometry.Bounds
import javafx.geometry.Point2D
import javafx.scene.Node

class AttachedNode(
    base: Node,
    val attachment: Node,
    val mode: Mode
) {
    val changeListener = ChangeListener<Bounds> { observable, old, it ->
        val line = when (mode.direction) {
            Direction.LEFT -> Point2D(it.minX, it.maxY).to(Point2D(it.minX, it.minY))
            Direction.RIGHT -> Point2D(it.maxX, it.minY).to(Point2D(it.maxX, it.maxY))
            Direction.TOP -> Point2D(it.minX, it.minY).to(Point2D(it.maxX, it.minY))
            Direction.BOTTOM -> Point2D(it.maxX, it.maxY).to(Point2D(it.minX, it.maxY))
        }

//        println("-> $line")
        val position = line.positionAt(mode.position, mode.distance, mode.offset)
//        println("--> $position")
        attachment.relocate(position.x, position.y)
    }

    init {
        base.boundsInParentProperty().addListener(changeListener)
    }

    companion object {
        data class Mode(
            val direction: Direction,
            val position: Percent,
            val distance: Double,
            val offset: Double = 0.0
        )

        fun attach(base: Node, attachment: Node, mode: Mode): AttachedNode {
            return AttachedNode(base, attachment, mode)
        }
    }
}