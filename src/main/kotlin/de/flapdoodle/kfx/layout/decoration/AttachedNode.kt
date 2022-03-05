package de.flapdoodle.kfx.layout.decoration

import de.flapdoodle.kfx.extensions.minus
import de.flapdoodle.kfx.types.Direction
import de.flapdoodle.kfx.types.Line2D
import de.flapdoodle.kfx.types.UnitInterval
import de.flapdoodle.kfx.types.to
import javafx.beans.value.ChangeListener
import javafx.geometry.Bounds
import javafx.geometry.Point2D
import javafx.scene.Node

class AttachedNode(
    base: Node,
    val attachment: Node,
    val mode: Mode,
    val attachmentMode: Mode
) {
    val changeListener = ChangeListener<Bounds> { observable, old, it ->
        val result = offset(it, mode, attachment.boundsInLocal, attachmentMode)
//        val line = borderLine(it, mode.direction)
//        val position = line.positionAt(mode.position, mode.distance, mode.offset)
//
//        val bounds = borderLine(attachment.boundsInLocal, attachmentMode.direction)
//        val attachmentPosition = bounds.positionAt(attachmentMode.position, attachmentMode.distance, attachmentMode.offset)
//
//        val result = position.minus(attachmentPosition)

        attachment.relocate(result.x, result.y)
    }

    init {
        base.boundsInParentProperty().addListener(changeListener)
    }

    companion object {
        data class Mode(
            val direction: Direction,
            val position: UnitInterval,
            val distance: Double,
            val offset: Double = 0.0
        )

        private fun borderLine(bounds: Bounds, direction: Direction): Line2D {
            return when (direction) {
                Direction.LEFT -> Point2D(bounds.minX, bounds.maxY).to(Point2D(bounds.minX, bounds.minY))
                Direction.RIGHT -> Point2D(bounds.maxX, bounds.minY).to(Point2D(bounds.maxX, bounds.maxY))
                Direction.TOP -> Point2D(bounds.minX, bounds.minY).to(Point2D(bounds.maxX, bounds.minY))
                Direction.BOTTOM -> Point2D(bounds.maxX, bounds.maxY).to(Point2D(bounds.minX, bounds.maxY))
            }
        }

        internal fun offset(
            sourceBounds: Bounds,
            sourceMode: Mode,
            attachmentBounds: Bounds,
            attachmentMode: Mode
        ): Point2D {
            val line = borderLine(sourceBounds, sourceMode.direction)
            val position = line.positionAt(sourceMode.position, sourceMode.distance, sourceMode.offset)

            val bounds = borderLine(attachmentBounds, attachmentMode.direction)
            val attachmentPosition = bounds.positionAt(attachmentMode.position, attachmentMode.distance, attachmentMode.offset)

            return position.minus(attachmentPosition)
        }

        fun attach(base: Node, attachment: Node, mode: Mode, attachmentMode: Mode): AttachedNode {
            return AttachedNode(base, attachment, mode, attachmentMode)
        }
    }
}