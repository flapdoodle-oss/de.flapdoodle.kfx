package de.flapdoodle.kfx.layout.virtual

import de.flapdoodle.kfx.bindings.mapToDouble
import de.flapdoodle.kfx.extensions.layoutPosition
import de.flapdoodle.kfx.extensions.minus
import de.flapdoodle.kfx.extensions.plus
import de.flapdoodle.kfx.extensions.screenPosition
import javafx.beans.InvalidationListener
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Bounds
import javafx.geometry.Orientation
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.ScrollBar
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.input.ZoomEvent
import javafx.scene.layout.Region
import javafx.scene.shape.Rectangle
import javafx.scene.transform.Scale

class PanZoomPanel(
    val sharedEventLock: SharedEventLock = SharedEventLock()
) : Region() {
    private val wrapper = Wrapper()

    private val zoom: DoubleProperty = object : SimpleDoubleProperty(1.0) {
        override fun invalidated() {
            requestLayout()
        }
    }

    private val scrollX = ScrollBar()
    private val scrollY = ScrollBar()

    init {
        styleClass.addAll("pan-zoom-panel")
        stylesheets += javaClass.getResource("PanZoomPanel.css").toExternalForm();

        val wrapperBounds: ReadOnlyObjectProperty<Bounds> = wrapper.boundsInParentProperty()

        wrapperBounds.addListener(InvalidationListener {
            requestLayout()
        })

        children.add(Rectangle().apply {
            styleClass.addAll("content-background")
            isManaged = false
            isMouseTransparent = true

            xProperty().bind(wrapperBounds.mapToDouble(Bounds::getMinX))
            yProperty().bind(wrapperBounds.mapToDouble(Bounds::getMinY))
            widthProperty().bind(wrapperBounds.mapToDouble(Bounds::getWidth))
            heightProperty().bind(wrapperBounds.mapToDouble(Bounds::getHeight))
        })

        wrapper.transforms.add(Scale().apply {
            xProperty().bind(zoom)
            yProperty().bind(zoom)
        })
        children.add(wrapper)

        scrollX.orientation = Orientation.HORIZONTAL
        scrollX.valueProperty().bindBidirectional(wrapper.layoutXProperty())
//        scrollX.styleClass.add("graph-editor-scroll-bar") //$NON-NLS-1$
        scrollY.orientation = Orientation.VERTICAL
        scrollY.valueProperty().bindBidirectional(wrapper.layoutYProperty())
        children.addAll(scrollX,scrollY)

        val clip = Rectangle()
        clip.widthProperty().bind(widthProperty())
        clip.heightProperty().bind(heightProperty())
        setClip(clip)

        addEventHandler(MouseEvent.ANY, this::handleMouseEvent)

        addEventHandler(ZoomEvent.ANY, this::handleZoom)
        addEventHandler(ScrollEvent.SCROLL, this::handleScroll)
    }

    fun setContent(node: Node) {
        wrapper.setContent(node)
    }

    override fun layoutChildren() {
        super.layoutChildren()

        scrollX.setBounds(
            ScrollBounds.of(
                windowSize = width,
                itemSize = wrapper.boundsInParent.width,
                itemOffset = zoom.get() * wrapper.boundsInLocal.minX,
                currentItemOffset = wrapper.layoutX
            )
        )

        scrollY.setBounds(
            ScrollBounds.of(
                windowSize = height,
                itemSize =  wrapper.boundsInParent.height,
                itemOffset = zoom.get() * wrapper.boundsInLocal.minY,
                currentItemOffset = wrapper.layoutY
            )
        )

//        wrapper.relocate(panZoomHandler.translateX(), panZoomHandler.translateY())

        val w = scrollY.width
        val h = scrollX.height

        scrollX.resizeRelocate(0.0, snapPositionY(height - h), snapSizeX(width - w), h)
        scrollY.resizeRelocate(snapPositionX(width - w), 0.0, w, snapSizeY(height - h))
    }

    fun zoom(zoom: Double) {
        setZoom(zoom)
    }

    fun zoomAt(zoom: Double, x: Double, y: Double) {
        setZoomAt(zoom, x, y)
    }


    private fun handleMouseEvent(event: MouseEvent) {
        when (event.eventType) {
            MouseEvent.MOUSE_PRESSED -> sharedEventLock.lock(this) {
                event.consume()

                cursor = Cursor.MOVE
                Action.Pan(
                    clickPosition = event.screenPosition,
                    posAtClick = wrapper.layoutPosition
                )
            }
            MouseEvent.MOUSE_DRAGGED -> sharedEventLock.ifLocked(this, Action::class.java) { current ->
                when (current) {
                    is Action.Pan -> {
                        event.consume()

                        val delta = event.screenPosition - current.clickPosition
                        val newPosition = current.posAtClick + delta
                        panTo(newPosition.x, newPosition.y)
                    }
                }
            }
            MouseEvent.MOUSE_RELEASED,
            MouseEvent.MOUSE_CLICKED -> sharedEventLock.release(this, Action::class.java) {
                when (it) {
                    is Action.Pan -> {
                        event.consume()

                        cursor = null
                    }
                }
            }
        }
    }


    private fun handleScroll(pEvent: ScrollEvent) {
        // this intended for mouse-scroll events (event direct == false)
        // the event also gets synthesized from touch events, which we want to ignore as they are handled in handleZoom()
        if (!pEvent.isDirect && pEvent.touchCount <= 0) {

            if (pEvent.isControlDown) {
                when (pEvent.eventType) {
                    ScrollEvent.SCROLL -> {
                        sharedEventLock.ifUnlocked {
                            if (pEvent.deltaY != 0.0) {
                                val direction = if (pEvent.deltaY > 1) ZoomDirection.In else ZoomDirection.Out
                                zoom(direction, pEvent.x, pEvent.y)
                            }
                            pEvent.consume()
                        }
                    }
                    ScrollEvent.SCROLL_STARTED -> {
                        sharedEventLock.lock(this) {

                            if (pEvent.deltaY != 0.0) {
                                val direction = if (pEvent.deltaY > 1) ZoomDirection.In else ZoomDirection.Out
                                zoom(direction, pEvent.x, pEvent.y)
                            }
                            pEvent.consume()
                            Action.Zoom
                        }
                    }
                    ScrollEvent.SCROLL_FINISHED -> {
                        sharedEventLock.release(this, Action::class.java) {
                            pEvent.consume()
                        }
                    }
                }
            } else {
                sharedEventLock.ifUnlocked {
                    panTo(translateX() + pEvent.deltaX, translateY() + pEvent.deltaY)
                    pEvent.consume()
                }
            }
        }
    }

    private fun handleZoom(pEvent: ZoomEvent) {
        when (pEvent.eventType) {
            ZoomEvent.ZOOM_STARTED -> {
                sharedEventLock.lock(this) {
                    pEvent.consume()
                    Action.Zoom
                }
            }
            ZoomEvent.ZOOM_FINISHED -> {
                sharedEventLock.release(this, Action::class.java) {
                    pEvent.consume()
                }
            }
            ZoomEvent.ZOOM -> {
                sharedEventLock.ifLocked(this, Action::class.java) { action ->
                    when (action) {
                        is Action.Zoom -> {
                            val newZoomLevel: Double = zoom.get() * pEvent.zoomFactor
                            setZoomAt(newZoomLevel, pEvent.x, pEvent.y)
                            pEvent.consume()
                        }
                    }
                }
            }
        }
    }


    fun panTo(x: Double, y: Double) {
        wrapper.layoutX = x
        wrapper.layoutY = y
    }

    fun translateX() = wrapper.layoutX
    fun translateY() = wrapper.layoutY

    fun zoomProperty() = zoom
    fun zoom() = zoom.get()


    fun setZoom(pZoom: Double) {
        setZoomAt(pZoom, translateX(), translateY())
    }

    enum class ZoomDirection {
        In, Out
    }

    fun zoom(direction: ZoomDirection, pPivotX: Double, pPivotY: Double) {
        val modifier = if (direction == ZoomDirection.In) 0.06 else -0.06
        val lastZoom = zoom.get()
        val newZoom = lastZoom + modifier
        setZoomAt(newZoom, pPivotX, pPivotY)
    }

    fun setZoomAt(pZoom: Double, pPivotX: Double, pPivotY: Double) {
        val oldZoomLevel: Double = zoom.get()
        val newZoomLevel = constrainZoom(pZoom)
        if (newZoomLevel != oldZoomLevel) {
            val f = newZoomLevel / oldZoomLevel - 1
            zoom.set(newZoomLevel)
            panTo(translateX() + f * pPivotX, translateY() + f * pPivotY)
        }
    }

    private fun constrainZoom(pZoom: Double): Double {
        val zoom = Math.round(pZoom * 100.0) / 100.0
        if (zoom <= 1.02 && zoom >= 0.98) {
            return 1.0
        }
        val ret =
            Math.min(Math.max(zoom, 0.5), 1.5)
        return ret
    }


    sealed class Action {
        data class Pan(val clickPosition: Point2D, val posAtClick: Point2D) : Action()
        object Zoom : Action()
    }



    class Wrapper : Region() {
        private var content: Node? = null

        init {
            isManaged = false
//            isMouseTransparent = true
            width = 10.0
            height = 10.0
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