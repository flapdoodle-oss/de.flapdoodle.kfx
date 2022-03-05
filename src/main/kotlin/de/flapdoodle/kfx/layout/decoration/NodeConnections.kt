package de.flapdoodle.kfx.layout.decoration

import javafx.scene.Node

object NodeConnections {
    fun attach(base: Node, attachment: Node, position: Position, attachmentPosition: Position): AttachedNode {
        return AttachedNode(base, attachment, position, attachmentPosition)
    }
}