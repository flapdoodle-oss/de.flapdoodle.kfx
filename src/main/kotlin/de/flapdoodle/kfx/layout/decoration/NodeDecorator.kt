package de.flapdoodle.kfx.layout.decoration

import de.flapdoodle.kfx.types.Direction
import javafx.beans.value.ChangeListener
import javafx.scene.Node

object NodeDecorator {
    fun attach(base: Node, attachment: Node) {
        base.boundsInParentProperty().addListener(ChangeListener { observable, oldValue, newValue ->
            attachment.relocate(newValue.minX+30, newValue.minY+30)
        })
    }
}