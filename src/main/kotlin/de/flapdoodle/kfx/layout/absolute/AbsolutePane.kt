package de.flapdoodle.kfx.layout.absolute

import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.control.Control

open class AbsolutePane : Control() {

    private val skin = AbsolutePaneSkin(this)
    override fun createDefaultSkin() = skin

    public override fun getChildren(): ObservableList<Node> {
        return super.getChildren()
    }
}