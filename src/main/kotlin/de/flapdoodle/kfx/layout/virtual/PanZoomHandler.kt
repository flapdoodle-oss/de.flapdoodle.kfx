package de.flapdoodle.kfx.layout.virtual

import de.flapdoodle.kfx.clone.GraphInputGesture
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.event.Event
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.input.MouseEvent

class PanZoomHandler(val panningWindow: PanningWindow) {

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

    private var clickPosition: Point2D? = null
    private var posAtClick: Point2D? = null
    private var gesture: Gesture? = null

    init {
        panningWindow.addEventHandler(MouseEvent.MOUSE_PRESSED) { event ->
            if (gesture==null) {
                gesture=Gesture.PAN
                startPanning(event.screenX, event.screenY)
            }
        }

        panningWindow.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handlePanningMouseDragged)

        panningWindow.addEventHandler(MouseEvent.MOUSE_RELEASED, this::handlePanningFinished)
        panningWindow.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handlePanningFinished)
    }

    private fun handlePanningMouseDragged(event: MouseEvent) {
        if (gesture==Gesture.PAN) {
            if (Cursor.MOVE != panningWindow.getCursor()) {
                startPanning(event.screenX, event.screenY)
            }
            val deltaX = event.screenX - clickPosition!!.x
            val deltaY = event.screenY - clickPosition!!.y
            val newWindowX: Double = posAtClick!!.x - deltaX
            val newWindowY: Double = posAtClick!!.y - deltaY
            panTo(newWindowX, newWindowY)
        }
    }

    private fun handlePanningFinished(event: Event) {
        gesture=null
        panningWindow.setCursor(null)
        event.consume()
    }

    private fun startPanning(x: Double, y: Double) {
        panningWindow.setCursor(Cursor.MOVE)
        clickPosition = Point2D(x, y)
        posAtClick = Point2D(translateX.get(), translateY.get())
    }

    fun panTo(x: Double, y: Double) {
        translateX.set(x)
        translateY.set(y)
    }

    fun translateX() = translateX.get()
    fun translateY() = translateY.get()
}