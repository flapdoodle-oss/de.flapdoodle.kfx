package de.flapdoodle.kfx.graph.nodes

import de.flapdoodle.kfx.events.SharedEventLock
import de.flapdoodle.kfx.extensions.localPosition
import de.flapdoodle.kfx.graph.nodes.Curves.bindControls
import de.flapdoodle.kfx.types.AngleAtPoint2D
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.Group
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.CubicCurve
import javafx.scene.shape.Shape
import kotlin.reflect.KClass

class Connections(
    val sharedEventLock: SharedEventLock = SharedEventLock(),
    val eventHandler: Connections.EventHandler
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
                if (matching!=null) {
                    cursor = when (eventHandler.onEvent(Event.OnConnector(matching))) {
                        is Response.OnConnector.IsConnectable -> Cursor.HAND
                        is Response.OnConnector.IsNotConnectable -> null
                    }
                }
            }

            when (event.eventType) {
                MouseEvent.MOUSE_PRESSED -> {
                    if (matching != null) {
                        val onStartConnect = eventHandler.onEvent(Event.StartConnect(matching))
                        if (onStartConnect.to.isNotEmpty()) {
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
                                if (openSocket!=null) {
                                    when (eventHandler.onEvent(Event.AtDestination(it.source, openSocket))) {
                                        is Response.AtDestination.IsConnectable -> {
                                            angle = openSocket.connectionPoint()
                                        }
                                        is Response.AtDestination.IsNotConnectable -> {

                                        }
                                    }
                                }
                                it.destination.value = angle
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
                                if (openSocket != null && openSocket != it.source) {
                                    val connected = eventHandler.onEvent(Event.Connect(it.source, openSocket))
//                                    // TODO delegate this to some external stuff
//                                    addConnection(it.source, openSocket)
                                }
                            }
                            is Action.Select -> {
                                val oldSelection = selection
                                if (oldSelection != null) {
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

    sealed class Event<R: Response> {
        data class OnConnector(val connector: Connector) : Event<Response.OnConnector>()
        data class StartConnect(val connector: Connector) : Event<Response.CanConnectTo>()
        data class AtDestination(val start: Connector, val destination: Connector) : Event<Response.AtDestination>()
        data class Connect(val start: Connector, val destination: Connector) : Event<Response.Connected>()
    }

    sealed class Response {
        sealed class OnConnector : Response() {
            data class IsConnectable(val source: Event.OnConnector) : OnConnector()
            data class IsNotConnectable(val source: Event.OnConnector) : OnConnector()
        }
        data class CanConnectTo(val source: Event.StartConnect, val to: List<Connector>) : Response()
        sealed class AtDestination : Response() {
            data class IsConnectable(val source: Event.AtDestination) : AtDestination()
            data class IsNotConnectable(val source: Event.AtDestination) : AtDestination()
        }
        data class Connected(val source: Event.Connect) : Response()
    }

    data class EventMatch<E: Event<R>, R: Response>(val type: KClass<E>, val action: (E) -> R) {
        fun respond(event: Event<out Response>): R? {
            return if (type.isInstance(event))
                action(event as E)
            else
                null
        }
    }

    interface EventHandler {
        fun <T: Event<R>, R: Response> onEvent(event: T): R

        companion object {
            fun with(vararg matches: EventMatch<out Event<out Response>, out Response>): EventHandler {
                return object : EventHandler {
                    override fun <T : Event<R>, R : Response> onEvent(event: T): R {
                        for (match in matches) {
                            val response = match.respond(event)
                            if (response!=null) return response as R
                        }
                        throw IllegalArgumentException("no match found for $event")
                    }
                }
            }
        }
    }
}