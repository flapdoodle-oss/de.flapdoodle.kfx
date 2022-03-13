package de.flapdoodle.kfx.graph.nodes

import de.flapdoodle.kfx.events.SharedEventLock
import de.flapdoodle.kfx.extensions.*
import de.flapdoodle.kfx.types.LayoutBounds
import de.flapdoodle.kfx.types.rawLayoutBounds
import de.flapdoodle.kfx.types.size
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Region

class Movables(
    val sharedEventLock: SharedEventLock = SharedEventLock(),
    val regionAsResizeable: (Region) -> Resizeable?
) : Group() {

    private var currentEnteredTarget: Movable? = null

    init {
        addEventHandler(MouseEvent.ANY, ::handleMouseEvent)
    }

    private fun handleMouseEvent(event: MouseEvent) {
        currentEnteredTarget?.let { targetAsRegion ->
            when (event.eventType) {
                MouseEvent.MOUSE_MOVED -> sharedEventLock.ifUnlocked {
                    val targetLocalPosition = targetAsRegion.node.parentToLocal(event.localPosition)
                    val sizeMode = SizeMode.guess(targetLocalPosition, targetAsRegion.node.size)
                    if (sizeMode != null) {
                        cursor = if (null != targetAsRegion.resizeable)
                            sizeMode.cursor()
                        else
                            SizeMode.INSIDE.cursor()
                    }
                }
                MouseEvent.MOUSE_PRESSED -> {
                    if (!event.isControlDown) {
                        sharedEventLock.lock(this) {
                            event.consume()

                            val targetLocalPosition = targetAsRegion.node.parentToLocal(event.localPosition)
                            val sizeMode = SizeMode.guess(targetLocalPosition, targetAsRegion.node.size)
                            if (sizeMode != null && sizeMode != SizeMode.INSIDE && targetAsRegion.resizeable != null) {
                                cursor = sizeMode.cursor()
                                Action.Resize(
                                    clickPosition = event.screenPosition,
                                    sizeMode = sizeMode,
                                    layout = targetAsRegion.node.rawLayoutBounds
                                )
                            } else
                                Action.Move(
                                    clickPosition = event.screenPosition,
                                    layoutPosition = targetAsRegion.node.layoutPosition
                                )
                        }
                    }
                }
                MouseEvent.MOUSE_DRAGGED -> sharedEventLock.ifLocked(this, Action::class.java) {
                    event.consume()
                    when (it) {
                        is Action.Move -> {
                            val diff = event.screenPosition - it.clickPosition
                            targetAsRegion.node.layoutPosition = it.layoutPosition + targetAsRegion.node.screenDeltaToLocal(diff)
                        }
                        is Action.Resize -> {
                            val diff = event.screenPosition - it.clickPosition
                            val fixedDiff = targetAsRegion.node.screenDeltaToLocal(diff)
                            val resizedBounds = SizeMode.resize(it.sizeMode, it.layout, fixedDiff)
                            targetAsRegion.node.layoutPosition = resizedBounds.layoutPosition
                            (targetAsRegion.resizeable ?: throw IllegalArgumentException("resizable not set"))
                                .resizeTo(
                                    resizedBounds.size.width, resizedBounds.size.height
                                )
                        }
                    }
                }
                MouseEvent.MOUSE_RELEASED -> sharedEventLock.release(this, Action::class.java) {
                    event.consume()
                    cursor = null
                }
            }
        }

        val target = children.firstOrNull { it == event.target }
        val targetAsRegion = target?.let {
            if (it is Region) it else null
        }

        if (targetAsRegion != null) {
//            println("event -> $event (lock: ${sharedEventLock.current})")

            when (event.eventType) {
                MouseEvent.MOUSE_ENTERED_TARGET -> sharedEventLock.ifUnlocked {
                    currentEnteredTarget = Movable(targetAsRegion, regionAsResizeable(targetAsRegion))

                    val targetLocalPosition = targetAsRegion.parentToLocal(event.localPosition)
                    val sizeMode = SizeMode.guess(targetLocalPosition, targetAsRegion.size)
                    if (sizeMode != null) {
                        cursor = sizeMode.cursor()
                    }
                }
                MouseEvent.MOUSE_EXITED_TARGET -> sharedEventLock.ifUnlocked {
                    currentEnteredTarget = null

                    cursor = null
                }
            }
        }
    }

    fun addAll(vararg elements: Node) {
        children.addAll(elements)
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

    data class Movable(val node: Region, val resizeable: Resizeable?)
}