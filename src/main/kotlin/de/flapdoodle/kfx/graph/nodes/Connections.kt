package de.flapdoodle.kfx.graph.nodes

import de.flapdoodle.kfx.events.SharedEventLock
import de.flapdoodle.kfx.extensions.*
import de.flapdoodle.kfx.graph.nodes.Curves.bindControls
import de.flapdoodle.kfx.types.AngleAtPoint2D
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.Group
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.CubicCurve
import javafx.scene.shape.Shape

class Connections(
    val connectableConnectors: ConnectableConnectors,
    val sharedEventLock: SharedEventLock = SharedEventLock()
) : Group() {

    private val sockets = Group()
    private val plugs = Group()
    private val connections = Group()

    private var selection: Connection? = null

    init {
        children.addAll(sockets, plugs, connections)
        addEventHandler(MouseEvent.ANY) { event ->
            val matching = socketAt(event.localPosition)
            val matchingConnection = connectionAt(event.localPosition)

            sharedEventLock.ifUnlocked {
                if (matching != null || matchingConnection != null) {
                    cursor = Cursor.HAND
                } else {
                    cursor = null
                }
            }

            when (event.eventType) {
                MouseEvent.MOUSE_PRESSED -> {
                    if (matching != null) {
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
                        when (it) {
                            is Action.Connect -> {
                                event.consume()

//                                val diff = event.screenPosition - it.clickPosition
//                                val currentPos = it.layoutPosition + screenDeltaToLocal(diff)
                                val currentPos = event.localPosition
                                val openSocket = socketAt(currentPos)

                                it.destination.value = (if (openSocket != null && openSocket != it.source) openSocket.connectionPoint()
                                else AngleAtPoint2D(currentPos, 0.0))
                            }
                        }
                    }
                }
                MouseEvent.MOUSE_RELEASED -> {
                    sharedEventLock.release(this, Action::class.java) {
                        event.consume()
                        when (it) {
                            is Action.Connect -> {
                                connections.children.remove(it.curve)

                                val currentPos = event.localPosition
                                val openSocket = socketAt(currentPos)
                                if (openSocket!=null && openSocket != it.source) {
                                    // TODO delegate this to some external stuff
                                    addConnection(it.source, openSocket)
                                }
                            }
                            is Action.Select -> {
                                val oldSelection = selection
                                if (oldSelection!=null) {
                                    oldSelection.stroke = Color.BLACK
                                }
                                if (oldSelection == it.connection) {
                                    selection = null
                                } else {
                                    selection = it.connection
                                    it.connection.stroke = Color.GREEN
                                }

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

    private fun connectionAt(localPosition: Point2D): Connection? {
        return connectionsAt(localPosition).firstOrNull()
    }

    private fun connectionsAt(localPosition: Point2D): List<Connection> {
        return connections.children.filterIsInstance<Connection>()
            .filter { !Shape.intersect(it, Circle(localPosition.x, localPosition.y, 5.0)).boundsInLocal.isEmpty }
    }

    fun addSocket(connector: Connector) {
        sockets.children.add(connector)
    }

    fun removeSocket(connector: Connector) {
        if (sockets.children.remove(connector)) {
            removeConnection { it.start == connector || it.end == connector }
        }
    }

    fun addPlug(connector: Connector) {

    }

    fun removePlug(connector: Connector) {
        if (plugs.children.remove(connector)) {
            removeConnection { it.start == connector || it.end == connector }
        }
    }

    fun addConnection(source: Connector, destination: Connector) {
        connections.children.addAll(Connection(source,destination))
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
        ): Action()
    }
}