package de.flapdoodle.kfx.layout.virtual

import de.flapdoodle.kfx.shapes.Curves
import javafx.scene.Group
import javafx.scene.paint.Color

@Deprecated("dont use")
class ConnectionPath(
    val start: Connector,
    val end: Connector
) : Group() {
    private val path = Curves.cubicCurve(
        start.connectionPointProperty(),
        end.connectionPointProperty()
    ).apply {
        strokeWidth = 1.0
        stroke = Color.BLACK
        fill = Color.TRANSPARENT
    }

    init {
        children.addAll(path)
    }
}