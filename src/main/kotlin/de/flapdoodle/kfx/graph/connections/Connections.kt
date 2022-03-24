package de.flapdoodle.kfx.graph.connections

import de.flapdoodle.kfx.events.SharedEventLock
import de.flapdoodle.kfx.extensions.localPosition
import de.flapdoodle.kfx.graph.nodes.Connector
import de.flapdoodle.kfx.graph.nodes.Curves
import de.flapdoodle.kfx.graph.nodes.Curves.bindControls
import de.flapdoodle.kfx.types.AngleAtPoint2D
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.Group
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.CubicCurve

class Connections(
    val sharedEventLock: SharedEventLock = SharedEventLock()
) : Group() {

    private val sockets = Group()
    private val connections = Group()
    private val selections = Group()

    private var connectionEventHandler: ConnectionEventHandler = DummyHandler()

    init {
        children.addAll(sockets, connections, selections)
        addEventHandler(MouseEvent.ANY) { event ->
            val matching = socketAt(event.localPosition)
            val matchingConnection = matchingConnection(event)

            sharedEventLock.ifUnlocked {
                if (matching!=null && connectionEventHandler.isConnectable(matching)) {
                    cursor = Cursor.HAND
                } else {
                    cursor = null
                }
            }

            when (event.eventType) {
                MouseEvent.MOUSE_PRESSED -> {
                    if (matching != null && connectionEventHandler.isConnectable(matching)) {
                        sharedEventLock.lock(this) {
                            event.consume()
                            Action.Connect(
                                clickPosition = event.localPosition,
                                source = matching,
                                destination = ReadOnlyObjectWrapper(AngleAtPoint2D(event.localPosition, 0.0))
                            ).also {
                                connections.children.addAll(it.curve)
                            }
                        }
                    } else {
                        if (matchingConnection != null) {
                            sharedEventLock.lock(this) {
                                Action.Select(matchingConnection)
                            }
                        }
                    }
                }
                MouseEvent.MOUSE_DRAGGED -> {
                    sharedEventLock.ifLocked(this, Action::class.java) {
                        event.consume()

                        when (it) {
                            is Action.Connect -> {

                                val currentPos = event.localPosition
                                val openSocket = socketAt(currentPos)



                                var angle = AngleAtPoint2D(currentPos, 0.0)
                                if (openSocket!=null && connectionEventHandler.connectableTo(it.source, openSocket)) {
                                    angle = openSocket.connectionPoint()
                                }
                                it.destination.value = angle
                            }
                        }
                    }
                }
                MouseEvent.MOUSE_RELEASED -> {
                    sharedEventLock.ifUnlocked {
                        event.consume()
                        clearSelection()
                    }

                    sharedEventLock.release(this, Action::class.java) {
                        event.consume()

                        when (it) {
                            is Action.Connect -> {
                                connections.children.remove(it.curve)

                                val currentPos = event.localPosition
                                val openSocket = socketAt(currentPos)
                                if (openSocket != null && openSocket != it.source) {
                                    connectionEventHandler.onConnect(it.source, openSocket)
                                }
                            }
                            is Action.Select -> {
                                connectionEventHandler.onSelect(it.connection)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun socketAt(localPosition: Point2D): Connector? {
        return socketsAt(localPosition).firstOrNull()
    }

    private fun socketsAt(localPosition: Point2D): List<Connector> {
        return sockets.children.filterIsInstance<Connector>()
            .filter { it.boundsInParent.contains(localPosition) }
    }

    private fun matchingConnection(event: MouseEvent): Connection? {
        return connections.children.filterIsInstance<Connection>()
            .filter { it -> it == event.target }
            .firstOrNull()
    }
    
    fun select(start: Connector, destination: Connector) {
        if (connections.children.filterIsInstance<Connection>()
            .filter { c -> c.start == start && c.end == destination }
            .isNotEmpty()) {
            selections.children.clear()
            selections.children.add(Connection(start,destination).apply {
                this.stroke = Color.GREEN
                this.isMouseTransparent = true
            })
        }
    }

    fun clearSelection() {
        selections.children.clear()
    }

    fun setHandler(handler: ConnectionEventHandler) {
        this.connectionEventHandler = handler
    }

    fun addConnector(connector: Connector) {
        sockets.children.add(connector)
    }

    fun removeSocket(connector: Connector) {
        if (sockets.children.remove(connector)) {
            removeConnection { it.start == connector || it.end == connector }
        }
    }

    fun addConnection(source: Connector, destination: Connector) {
        connections.children.addAll(Connection(source, destination))
    }

    fun removeConnection(source: Connector, destination: Connector) {
        removeConnection { it.start == source && it.end == destination }
    }

    private fun removeConnection(match: (Connection) -> Boolean) {
        val matching: List<Connection> = connections.children.filterIsInstance<Connection>()
            .filter { match(it) }

        connections.children.removeAll(matching)
    }

    class Connection(
        val start: Connector,
        val end: Connector
    ) : CubicCurve() {
        init {
            bindControls(start.connectionPointProperty(), end.connectionPointProperty())

            strokeWidth = 1.0
            stroke = Color.BLACK
            fill = Color.TRANSPARENT
        }
    }

    sealed class Action {

        data class Connect(
            val clickPosition: Point2D,
            val source: Connector,
            val destination: ReadOnlyObjectWrapper<AngleAtPoint2D>
        ) : Action() {
            val curve = Curves.cubicCurve(source.connectionPointProperty(), destination).also {
                it.stroke = Color.RED
                it.fill = Color.TRANSPARENT
                it.strokeWidth = 1.0
            }
        }

        data class Select(
            val connection: Connection
        ) : Action()
    }

    class DummyHandler : ConnectionEventHandler {
        override fun isConnectable(matching: Connector): Boolean {
            return true
        }

        override fun connectableTo(source: Connector, destination: Connector): Boolean {
            return true
        }

        override fun onConnect(source: Connector, destination: Connector) {
        }

        override fun onSelect(selection: Connection) {
        }
    }
}