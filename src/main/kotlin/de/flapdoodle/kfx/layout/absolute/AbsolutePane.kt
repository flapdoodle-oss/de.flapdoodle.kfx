package de.flapdoodle.kfx.layout.absolute

import de.flapdoodle.kfx.extensions.markAsContainer
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.control.Control

open class AbsolutePane : Control() {
    init {
        markAsContainer()
    }

    private val skin = AbsolutePaneSkin(this)
    override fun createDefaultSkin() = skin

    public override fun getChildren(): ObservableList<Node> {
        return super.getChildren()
    }

    override fun computePrefWidth(height: Double): Double {
        layout()

        val result = layoutBounds.width
        return if (java.lang.Double.isNaN(result) || result < 0) 0.0 else result
    }

    override fun computePrefHeight(width: Double): Double {
        layout()

        val result = layoutBounds.height
        return if (java.lang.Double.isNaN(result) || result < 0) 0.0 else result
    }

    override fun computeMinWidth(height: Double): Double {
        return prefWidth(height)
    }

    override fun computeMinHeight(width: Double): Double {
        return prefHeight(width)
    }
}