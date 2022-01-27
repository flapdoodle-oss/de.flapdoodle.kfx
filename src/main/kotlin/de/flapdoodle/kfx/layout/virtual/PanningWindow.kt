package de.flapdoodle.kfx.layout.virtual

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.Node
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle

open class PanningWindow : Region() {
    val wrapper = Wrapper()
    val panZoomHandler = PanZoomHandler(this)

    init {
        children.add(wrapper)

        val clip = Rectangle()
        clip.widthProperty().bind(widthProperty())
        clip.heightProperty().bind(heightProperty())
        setClip(clip)
    }

    fun setContent(node: Node) {
        wrapper.setContent(node)
    }

    override fun layoutChildren() {
        super.layoutChildren()

        wrapper.relocate(-panZoomHandler.translateX(), -panZoomHandler.translateY());
    }

    class Wrapper : Region() {
        private var content: Node? = null

        init {
            isManaged = false
            border = Border(BorderStroke(Color.GREEN, BorderStrokeStyle.DASHED, CornerRadii(1.0), BorderWidths(1.0)))
            width = 10.0
            height = 10.0
            children.addAll(Line(-100.0, -100.0, 100.0, 100.0).apply {
                strokeWidth = 1.0
                stroke = Color.GREEN
                strokeDashArray.addAll(5.0, 5.0)
            })
            children.addAll(Line(100.0, -100.0, -100.0, 100.0).apply {
                strokeWidth = 1.0
                stroke = Color.GREEN
                strokeDashArray.addAll(5.0, 5.0)
            })
        }

        fun setContent(node: Node) {
            removeContent()
            content = node
            children.addAll(node)
        }

        fun removeContent() {
            if (content!=null) children.remove(content)
            content=null
        }
    }
}