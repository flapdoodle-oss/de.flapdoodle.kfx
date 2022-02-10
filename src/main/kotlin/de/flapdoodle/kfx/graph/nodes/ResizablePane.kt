package de.flapdoodle.kfx.graph.nodes

import de.flapdoodle.kfx.layout.virtual.SharedEventLock
import de.flapdoodle.kfx.types.LayoutBounds
import de.flapdoodle.kfx.types.rawLayoutBounds
import javafx.geometry.Point2D
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.scene.shape.Rectangle

class ResizablePane(val sharedEventLock: SharedEventLock = SharedEventLock()) : StackPane() {
    private val DEFAULT_RESIZE_BORDER_TOLERANCE = 8

    init {
        isPickOnBounds = false

        addEventHandler(MouseEvent.MOUSE_ENTERED, this::handleMouseEntered)
        addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressed)
        addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleMouseDragged)
        addEventHandler(MouseEvent.MOUSE_RELEASED, this::handleMouseReleased)
        addEventHandler(MouseEvent.MOUSE_MOVED, this::handleMouseMoved)
        addEventHandler(MouseEvent.MOUSE_EXITED, this::handleMouseExited)

        val border = Rectangle()
        val background = Rectangle()

        background.widthProperty().bind(border.widthProperty().subtract(border.strokeWidthProperty().multiply(2)))
        background.heightProperty().bind(border.heightProperty().subtract(border.strokeWidthProperty().multiply(2)))

        border.widthProperty().bind(this.widthProperty())
        border.heightProperty().bind(this.heightProperty())

        border.getStyleClass()
            .setAll("default-node-border")
        background.getStyleClass()
            .setAll("default-node-background")

        this.getChildren().addAll(border, background)
        this.setMinSize(
            30.0,
            30.0
        )
//        this.setPrefSize(40.0, 40.0)
        this.resize(60.0, 60.0)
        this.layoutX = 20.0
        this.layoutY = 30.0

//        background.addEventFilter<MouseEvent>(
//            MouseEvent.MOUSE_DRAGGED,
//            EventHandler { event: MouseEvent? ->
//                if (event!!.isPrimaryButtonDown && /*!isSelected()*/false) {
//                    event.consume()
//                }
//            })

    }

    private fun handleMouseExited(event: MouseEvent) {
        sharedEventLock.ifUnlocked {
            cursor = null
        }

    }

    private fun handleMouseEntered(event: MouseEvent) {
        sharedEventLock.ifUnlocked {
            val sizeMode = SizeMode.guess(event.x, event.y, width, height)
            if (sizeMode != null) {
                cursor = sizeMode.cursor()
            }
        }
    }

    private fun handleMouseMoved(event: MouseEvent) {
        sharedEventLock.ifUnlocked {
            val sizeMode = SizeMode.guess(event.x, event.y, width, height)
            if (sizeMode != null) {
                cursor = sizeMode.cursor()
            }
        }
    }

    private fun handleMousePressed(event: MouseEvent) {
        sharedEventLock.lock(this) {
            event.consume()
//            println("press -> ${event.screenX},${event.screenY}")
            val sizeMode = SizeMode.guess(event.x, event.y, width, height)
            if (sizeMode != null && sizeMode != SizeMode.INSIDE) {
                cursor = sizeMode.cursor()
                Action.Resize(
                    clickPosition = Point2D(event.screenX, event.screenY),
                    sizeMode = sizeMode,
                    layout = rawLayoutBounds
                )
            } else
                Action.Move(
                    clickPosition = Point2D(event.screenX, event.screenY),
                    layoutPosition = Point2D(layoutX, layoutY)
                )
        }
    }

    private fun handleMouseDragged(event: MouseEvent) {
        sharedEventLock.ifLocked(this, Action::class.java) {
//            println("dragged -> ${event.screenX},${event.screenY} - $it")
            when (it) {
                is Action.Move -> {
                    val diffX = event.screenX - it.clickPosition.x
                    val diffY = event.screenY - it.clickPosition.y
                    val newLayoutX = it.layoutPosition.x + diffX
                    val newLayoutY = it.layoutPosition.y + diffY
                    layoutX = newLayoutX
                    layoutY = newLayoutY
                }
                is Action.Resize -> {
                    val diffX = event.screenX - it.clickPosition.x
                    val diffY = event.screenY - it.clickPosition.y
                    val resizedBounds = SizeMode.resize(it.sizeMode, it.layout, diffX, diffY)
                    this.setRawLayoutBounds(resizedBounds)
                }
            }
            event.consume()
        }
    }

    private fun handleMouseReleased(event: MouseEvent) {
        sharedEventLock.release(this, Action::class.java) {
//            println("release -> ${event.y},${event.y}")
            // finish action?
            event.consume()
            cursor = null
        }
    }

    private fun setRawLayoutBounds(bounds: LayoutBounds) {
        layoutX = bounds.x
        layoutY = bounds.y
        width = bounds.width
        height = bounds.height
    }

    sealed class Action {
        data class Move(
            val clickPosition: Point2D,
            val layoutPosition: Point2D
        ) : Action()

        data class Resize(
            val clickPosition: Point2D,
            val sizeMode: SizeMode,
            val layout: LayoutBounds
        ) : Action()
    }
}