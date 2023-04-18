package de.flapdoodle.kfx.nodeeditor.events

import de.flapdoodle.kfx.nodeeditor.Node
import de.flapdoodle.kfx.nodeeditor.NodeConnection
import javafx.event.Event
import javafx.event.EventType

class NodeConnectionEvent(eventType: EventType<NodeConnectionEvent>, val connection: NodeConnection) : Event(eventType) {
  companion object {
    val ANY=EventType<NodeConnectionEvent>("CONNECTION_ANY")
    val ADDED=EventType(ANY,"CONNECTION_ADDED")
    val REMOVED=EventType(ANY,"CONNECTION_REMOVED")
  }
}