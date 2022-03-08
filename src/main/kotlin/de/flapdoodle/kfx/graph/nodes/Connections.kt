package de.flapdoodle.kfx.graph.nodes

import de.flapdoodle.kfx.events.SharedEventLock
import de.flapdoodle.kfx.extensions.*
import de.flapdoodle.kfx.types.AngleAtPoint2D
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color

class Connections(
    val sharedEventLock: SharedEventLock = SharedEventLock()
) : Group() {

    private val eventHandler = EventHandler<MouseEvent> { event ->
        val connector = (event.source as Connector)
        when (connector.constraint[ConnectorType::class]) {
            ConnectorType.Plug -> handlePlug(event, connector)
            ConnectorType.Socket -> handleSocket(event, connector)
        }
    }

    private fun handlePlug(event: MouseEvent, connector: Connector) {
        when (event.eventType) {
            MouseEvent.MOUSE_PRESSED -> sharedEventLock.lock(this) {
                event.consume()
                Action.Move(
                    clickPosition = event.screenPosition,
                    layoutPosition = connector.layoutPosition
                )
            }
            MouseEvent.MOUSE_DRAGGED -> sharedEventLock.ifLocked(this, Action::class.java) {
                when (it) {
                    is Action.Move -> {
                        event.consume()

                        val diff = event.screenPosition - it.clickPosition
                        connector.layoutPosition = it.layoutPosition + screenDeltaToLocal(diff)
                    }
                }
            }
            MouseEvent.MOUSE_RELEASED -> sharedEventLock.release(this, Action::class.java) {
                event.consume()
            }
        }
    }

    private fun handleSocket(event: MouseEvent, connector: Connector) {
        when (event.eventType) {
            MouseEvent.MOUSE_PRESSED -> sharedEventLock.lock(this) {
                event.consume()
                Action.Connect(
                    clickPosition = event.screenPosition,
                    layoutPosition = connector.layoutPosition,
                    source = connector,
                    destination = ReadOnlyObjectWrapper(AngleAtPoint2D(connector.layoutPosition, 0.0))
                ).also {
                    children.addAll(it.curve)
                }
            }
            MouseEvent.MOUSE_DRAGGED -> sharedEventLock.ifLocked(this, Action::class.java) {
                when (it) {
                    is Action.Connect -> {
                        event.consume()

                        val diff = event.screenPosition - it.clickPosition
                        val currentPos = it.layoutPosition + screenDeltaToLocal(diff)

                        val openSocket = //                            .filter { it.constraint[ConnectorType::class] == ConnectorType.Socket }
                            children.filtered { it is Connector }
                                .map { it as Connector }
                                .filter { it !== connector }
                                .firstOrNull { it.boundsInParent.intersects(currentPos.x, currentPos.y, 2.0, 2.0) }

                        it.destination.value = (if (openSocket != null) openSocket.connectionPoint()
                        else AngleAtPoint2D(currentPos, 0.0))
                    }
                }
            }
            MouseEvent.MOUSE_RELEASED -> sharedEventLock.release(this, Action::class.java) {
                event.consume()
                when (it) {
                    is Action.Connect -> {
                        children.remove(it.curve)
                    }
                }
            }
        }
    }


    private fun rotateConnector(source: Connector) {
        source.angle(source.angle() + 15.0)
    }

    fun addConnectorAt(pos: Point2D, connectorType: ConnectorType): Connector {
        return Connector().also {
            it.relocate(pos.x, pos.y)
            it.addEventHandler(MouseEvent.ANY, eventHandler)
            it.constraint[ConnectorType::class] = connectorType
            children.addAll(it)
        }
    }

    sealed class Action {
        data class Move(
            val clickPosition: Point2D,
            val layoutPosition: Point2D
        ) : Action()

        data class Connect(
            val clickPosition: Point2D,
            val layoutPosition: Point2D,
            val source: Connector,
            val destination: ReadOnlyObjectWrapper<AngleAtPoint2D>
        ) : Action() {
            val curve = Curves.cubicCurve(source.connectionPointProperty(), destination).also {
                it.stroke = Color.RED
                it.fill = Color.TRANSPARENT
                it.strokeWidth = 1.0
            }
        }
    }

    enum class ConnectorType {
        Plug, Socket
    }
}