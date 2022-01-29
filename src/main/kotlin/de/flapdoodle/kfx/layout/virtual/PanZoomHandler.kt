package de.flapdoodle.kfx.layout.virtual

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.event.Event
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.input.MouseEvent

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

    init {
        panningWindow.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressed)
        panningWindow.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handlePanningMouseDragged)
        panningWindow.addEventHandler(MouseEvent.MOUSE_RELEASED, this::handlePanningFinished)
        panningWindow.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handlePanningFinished)
    }

    private fun handleMousePressed(event: MouseEvent) {
        sharedEventLock.lock(panningWindow) {
            panningWindow.setCursor(Cursor.MOVE)
            State(
                gesture = Gesture.PAN,
                clickPosition = Point2D(event.screenX, event.screenY),
                posAtClick = Point2D(translateX.get(), translateY.get())
            )
        }
    }

    private fun handlePanningMouseDragged(event: MouseEvent) {
        sharedEventLock.ifLocked(panningWindow, State::class.java) { current ->
            when (current.gesture) {
                Gesture.PAN -> {
                    val deltaX = event.screenX - current.clickPosition.x
                    val deltaY = event.screenY - current.clickPosition.y
                    val newWindowX: Double = current.posAtClick.x - deltaX
                    val newWindowY: Double = current.posAtClick.y - deltaY
                    panTo(newWindowX, newWindowY)
                }
            }
        }
    }

    private fun handlePanningFinished(event: Event) {
        sharedEventLock.release(panningWindow, State::class.java) {
            panningWindow.setCursor(null)
            event.consume()
        }
    }

    fun panTo(x: Double, y: Double) {
        translateX.set(x)
        translateY.set(y)
    }

    fun translateX() = translateX.get()
    fun translateY() = translateY.get()

    private data class State(
        val gesture: Gesture,
        val clickPosition: Point2D,
        val posAtClick: Point2D)
}