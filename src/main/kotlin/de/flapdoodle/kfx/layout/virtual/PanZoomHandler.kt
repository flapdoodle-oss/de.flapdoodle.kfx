package de.flapdoodle.kfx.layout.virtual

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.event.Event
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.input.ZoomEvent

class PanZoomHandler(
    val panningWindow: PanningWindow,
    val sharedEventLock: SharedEventLock = SharedEventLock()
) {

    private val translateX: DoubleProperty = object : SimpleDoubleProperty() {
        override fun invalidated() {
            panningWindow.requestLayout()
        }
    }

    private val translateY: DoubleProperty = object : SimpleDoubleProperty() {
        override fun invalidated() {
            panningWindow.requestLayout()
        }
    }

    private val zoom: DoubleProperty = object : SimpleDoubleProperty(1.0) {
        override fun invalidated() {
            panningWindow.requestLayout()
        }
    }

    init {
        panningWindow.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressed)
        panningWindow.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handlePanningMouseDragged)
        panningWindow.addEventHandler(MouseEvent.MOUSE_RELEASED, this::handlePanningFinished)
        panningWindow.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handlePanningFinished)

        panningWindow.addEventHandler(ZoomEvent.ANY, this::handleZoom)
        panningWindow.addEventHandler(ScrollEvent.SCROLL, this::handleScroll)

    }

    private fun handleMousePressed(event: MouseEvent) {
        sharedEventLock.lock(panningWindow) {
            panningWindow.setCursor(Cursor.MOVE)
            Action.Pan(
                clickPosition = Point2D(event.screenX, event.screenY),
                posAtClick = Point2D(translateX.get(), translateY.get())
            )
        }
    }

    private fun handlePanningMouseDragged(event: MouseEvent) {
        sharedEventLock.ifLocked(panningWindow, Action::class.java) { current ->
            when (current) {
                is Action.Pan -> {
                    val deltaX = event.screenX - current.clickPosition.x
                    val deltaY = event.screenY - current.clickPosition.y
                    val newWindowX: Double = current.posAtClick.x + deltaX
                    val newWindowY: Double = current.posAtClick.y + deltaY
                    panTo(newWindowX, newWindowY)
                }
            }
        }
    }

    private fun handlePanningFinished(event: Event) {
        sharedEventLock.release(panningWindow, Action::class.java) {
            when (it) {
                is Action.Pan -> {
                    panningWindow.setCursor(null)
                    event.consume()
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
                                val direction = if (pEvent.deltaY>1) ZoomDirection.In else ZoomDirection.Out
                                zoom(direction, pEvent.x, pEvent.y)
                            }
                            pEvent.consume()
                        }
                    }
                    ScrollEvent.SCROLL_STARTED -> {
                        sharedEventLock.lock(panningWindow) {

                            if (pEvent.deltaY != 0.0) {
                                val direction = if (pEvent.deltaY>1) ZoomDirection.In else ZoomDirection.Out
                                zoom(direction, pEvent.x, pEvent.y)
                            }
                            pEvent.consume()
                            Action.Zoom
                        }
                    }
                    ScrollEvent.SCROLL_FINISHED -> {
                        sharedEventLock.release(panningWindow, Action::class.java) {
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
                sharedEventLock.lock(panningWindow) {
                    pEvent.consume()
                    Action.Zoom
                }
            }
            ZoomEvent.ZOOM_FINISHED -> {
                sharedEventLock.release(panningWindow, Action::class.java) {
                    pEvent.consume()
                }
            }
            ZoomEvent.ZOOM -> {
                sharedEventLock.ifLocked(panningWindow, Action::class.java) { action ->
                    when(action) {
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
        translateX.set(x)
        translateY.set(y)
    }

    fun translateX() = translateX.get()
    fun translateY() = translateY.get()

    fun translateXProperty() = translateX
    fun translateYProperty() = translateY

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


//    private data class State(
//        val gesture: Gesture,
//        val clickPosition: Point2D,
//        val posAtClick: Point2D)

    sealed class Action {
        data class Pan(val clickPosition: Point2D, val posAtClick: Point2D) : Action()
        object Zoom : Action()
    }
}