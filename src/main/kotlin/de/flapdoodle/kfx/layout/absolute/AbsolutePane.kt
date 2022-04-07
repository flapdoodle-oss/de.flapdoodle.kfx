package de.flapdoodle.kfx.layout.absolute

import com.sun.javafx.geom.BaseBounds
import com.sun.javafx.geom.transform.BaseTransform
import de.flapdoodle.kfx.extensions.HasChildsInParentBounds
import javafx.collections.ObservableList
import javafx.geometry.Bounds
import javafx.scene.Node
import javafx.scene.control.Control

open class AbsolutePane : Control(), HasChildsInParentBounds {

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