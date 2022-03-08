package de.flapdoodle.kfx.graph.nodes

import de.flapdoodle.kfx.events.SharedEventLock
import de.flapdoodle.kfx.extensions.*
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.input.MouseEvent

class Connections(
    val sharedEventLock: SharedEventLock = SharedEventLock()
) : Group() {

    private val eventHandler = EventHandler<MouseEvent> { event ->
        when (event.eventType) {
//            MouseEvent.MOUSE_CLICKED -> sharedEventLock.ifUnlocked {
//                event.consume()
//                rotateConnector(event.source as Connector)
//            }
            MouseEvent.MOUSE_PRESSED -> {
                val connector = (event.source as Connector)
                if (connector.constraint[ConnectorType::class] == ConnectorType.Plug) {
                    sharedEventLock.lock(this) {
                        event.consume()
                        Action.Move(
                            clickPosition = event.screenPosition,
                            layoutPosition = connector.layoutPosition
                        )
                    }
                }
            }
            MouseEvent.MOUSE_DRAGGED -> sharedEventLock.ifLocked(this, Action::class.java) {
                when (it) {
                    is Action.Move -> {
                        val connector = (event.source as Connector)
                        val diff = event.screenPosition - it.clickPosition
                        connector.layoutPosition = it.layoutPosition + screenDeltaToLocal(diff)
                        event.consume()
                    }
                }
            }
            MouseEvent.MOUSE_RELEASED -> sharedEventLock.release(this, Action::class.java) {
                event.consume()
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
    }

    enum class ConnectorType {
        Plug, Socket
    }
}