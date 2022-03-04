package de.flapdoodle.kfx.graph.nodes

import javafx.scene.Group
import javafx.scene.shape.CubicCurve
import javafx.scene.shape.Path
import javafx.scene.shape.PathElement
import javafx.scene.shape.QuadCurve

class ConnectionPath : Group() {
    private val path = Path()

    init {
        children.addAll(path)

        isAutoSizeChildren = false

//        path.elements.addAll(CubicCurve())
    }
}