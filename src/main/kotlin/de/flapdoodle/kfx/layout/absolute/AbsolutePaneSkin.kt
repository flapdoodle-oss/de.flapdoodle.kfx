package de.flapdoodle.kfx.layout.absolute

import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.control.SkinBase

class AbsolutePaneSkin(val control: AbsolutePane) : SkinBase<AbsolutePane>(control) {

    override fun layoutChildren(contentX: Double, contentY: Double, contentWidth: Double, contentHeight: Double) {
        children.forEach { child ->
            if (child.isManaged) {
                val x = child.layoutX
                val y = child.layoutY
                val w = child.prefWidth(contentHeight)
                val h = child.prefHeight(contentWidth)
                layoutInArea(child, x, y, w, h, -1.0, HPos.CENTER, VPos.CENTER)
            }
        }
    }
}