package de.flapdoodle.kfx.layout.virtual

import de.flapdoodle.kfx.bindings.mapToDouble
import javafx.beans.binding.Bindings
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Bounds
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.ScrollBar
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import javafx.scene.transform.Scale

open class PanningWindow : Region() {
    private val wrapper = Wrapper()
    private val panZoomHandler = PanZoomHandler(this)
    private val scale = Scale()

    private val minX = SimpleDoubleProperty()
    private val maxX = SimpleDoubleProperty()

    private val scrollX = ScrollBar()
    private val scrollY = ScrollBar()

    init {
        val contentBorder = Rectangle().apply {
            fill = Color.rgb(255,255,255,0.9)
        }
        val wrapperBounds: ReadOnlyObjectProperty<Bounds> = wrapper.boundsInParentProperty()
        val boundsMinX = wrapperBounds.mapToDouble(Bounds::getMinX)
        val boundsMinY = wrapperBounds.mapToDouble(Bounds::getMinY)
        val boundsWidht = wrapperBounds.mapToDouble(Bounds::getWidth)
        val boundsHeight = wrapperBounds.mapToDouble(Bounds::getHeight)

//        val lowerBound = boundsMinX.multiply(-1.0)
//        val upperBound = widthProperty().subtract(wrapperBounds.mapToDouble(Bounds::getMaxX))

        scrollX.orientation = Orientation.HORIZONTAL
//        scrollX.minProperty().bind(lowerBound)
//        scrollX.maxProperty().bind(upperBound)
        scrollX.valueProperty().bind(panZoomHandler.translateXProperty())
//        scrollX.valueProperty().bindBidirectional(panZoomHandler.translateXProperty())
//        scrollX.styleClass.add("graph-editor-scroll-bar") //$NON-NLS-1$
        scrollY.orientation = Orientation.VERTICAL
        scrollY.valueProperty().bind(panZoomHandler.translateYProperty())

        children.addAll(scrollX,scrollY)

        with(contentBorder) {
            layoutXProperty().bind(boundsMinX)
            layoutYProperty().bind(boundsMinY)
            widthProperty().bind(boundsWidht)
            heightProperty().bind(boundsHeight)
        }
        contentBorder.isManaged = false

        children.add(contentBorder)

        minX.bind(boundsMinX)
        maxX.bind(Bindings.max(boundsMinX.add(boundsWidht), widthProperty()))

        val clip = Rectangle()
        clip.widthProperty().bind(widthProperty())
        clip.heightProperty().bind(heightProperty())
        setClip(clip)

        scale.xProperty().bind(panZoomHandler.zoomProperty())
        scale.yProperty().bind(panZoomHandler.zoomProperty())
        wrapper.transforms.add(scale)
        children.add(wrapper)
    }

    fun foo() {

    }

    fun setContent(node: Node) {
        wrapper.setContent(node)
    }

    override fun layoutChildren() {
        super.layoutChildren()
        println("x -> ${minX.get()} ... ${panZoomHandler.translateX()} ... ${maxX.get()}")

        wrapper.relocate(-panZoomHandler.translateX(), -panZoomHandler.translateY())

        // scrollbars

        // scrollbars
        val w = scrollY.width
        val h = scrollX.height

        scrollX.resizeRelocate(0.0, snapPositionY(height - h), snapSizeX(width - w), h)
        scrollY.resizeRelocate(snapPositionX(width - w), 0.0, w, snapSizeY(height - h))

        println("scroll: ${scrollX.min} - ${scrollX.value} - ${scrollX.max}")
//        val zoomFactor: Double = if (theContent == null) 1 else theContent.getLocalToSceneTransform().getMxx()
//        scrollX.min = 0.0
//        scrollX.max = getMaxX()
//        scrollX.visibleAmount = zoomFactor * width
//        scrollY.setMin(0.0)
//        scrollY.setMax(getMaxY())
//        scrollY.setVisibleAmount(zoomFactor * height)

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