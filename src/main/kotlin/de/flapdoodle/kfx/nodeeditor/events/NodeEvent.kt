package de.flapdoodle.kfx.nodeeditor.events

import de.flapdoodle.kfx.nodeeditor.Node
import javafx.event.Event
import javafx.event.EventType

class NodeEvent(eventType: EventType<NodeEvent>, val node: Node) : Event(eventType) {
  companion object {
    val ANY=EventType<NodeEvent>("NODE_ANY")
    val NODE_ADDED=EventType(ANY,"NODE_ADDED")
    val NODE_REMOVED=EventType(ANY,"NODE_REMOVED")
  }
}