package de.flapdoodle.kfx.nodeeditor

import de.flapdoodle.kfx.bindings.map
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.ReadOnlyMapWrapper
import javafx.collections.FXCollections
import javafx.collections.ObservableMap
import java.util.*

class NodeRegistry {
  private val nodes: ObservableMap<UUID, Node> = FXCollections.observableHashMap()
  private val nodesPropery = ReadOnlyMapWrapper(nodes)

  fun registerNode(node: Node) {
    nodes[node.uuid] = node
  }

  fun unregisterNode(node: Node) {
    nodes.remove(node.uuid)
  }

  fun registerConnection(connection: NodeConnection) {
    connection.init(this::nodeByIdProperty)
  }

  fun unregisterConnection(connection: NodeConnection) {
    connection.dispose()
  }

  private fun nodeByIdProperty(id: UUID): ObjectBinding<Node?> {
    return nodesPropery.map { it[id] }
  }

}