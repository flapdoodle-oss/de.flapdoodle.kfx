package de.flapdoodle.kfx.nodeeditor

import de.flapdoodle.kfx.bindings.map
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.ReadOnlyMapWrapper
import javafx.collections.FXCollections
import javafx.collections.ObservableMap
import java.util.*

class Model {
  private val nodes: ObservableMap<UUID, Node> = FXCollections.observableHashMap<UUID, Node>()
  private val nodesPropery = ReadOnlyMapWrapper(nodes);

  fun registerNode(node: Node) {
    println("register node: ${node.uuid} to ${node.name}")
    nodes[node.uuid] = node
  }

  fun unregisterNode(node: Node) {
    nodes.remove(node.uuid)
  }

  fun registerConnection(connection: NodeConnection) {
    println("register connection ${connection.name} - ${connection.start} to ${connection.end}")
    connection.init(this::nodeByIdProperty)
  }

  fun unregisterConnection(connection: NodeConnection) {
    // TODO .. must remove listener..
  }

  private fun nodeByIdProperty(id: UUID): ObjectBinding<Node?> {
    return nodesPropery.map { it[id] }
  }

}