package de.flapdoodle.kfx.layout.virtual

import de.flapdoodle.kfx.bindings.mapToDouble
import javafx.beans.binding.Bindings
import javafx.beans.binding.DoubleBinding
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Bounds
import javafx.scene.Node
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import javafx.scene.transform.Scale

open class PanningWindow : Region() {
    private val wrapper = Wrapper()
    private val panZoomHandler = PanZoomHandler(this)
    private val scale = Scale()

    init {
        val contentBorder = Rectangle().apply {
            fill = Color.rgb(255,255,255,0.9)
        }
        val inParent: ReadOnlyObjectProperty<Bounds> = wrapper.boundsInParentProperty()

        contentBorder.layoutXProperty().bind(inParent.mapToDouble { it.minX })
        contentBorder.layoutYProperty().bind(inParent.mapToDouble { it.minY })
        contentBorder.widthProperty().bind(inParent.mapToDouble { it.width })
        contentBorder.heightProperty().bind(inParent.mapToDouble { it.height })
        contentBorder.isManaged = false

        children.add(contentBorder)

        val clip = Rectangle()
        clip.widthProperty().bind(widthProperty())
        clip.heightProperty().bind(heightProperty())
        setClip(clip)

        scale.xProperty().bind(panZoomHandler.zoomProperty())
        scale.yProperty().bind(panZoomHandler.zoomProperty())
        wrapper.transforms.add(scale)
        children.add(wrapper)
    }

    fun setContent(node: Node) {
        wrapper.setContent(node)
    }

    override fun layoutChildren() {
        super.layoutChildren()

        wrapper.relocate(-panZoomHandler.translateX(), -panZoomHandler.translateY())
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