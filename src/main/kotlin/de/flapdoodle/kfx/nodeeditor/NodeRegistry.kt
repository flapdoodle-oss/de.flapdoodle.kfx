package de.flapdoodle.kfx.nodeeditor

import de.flapdoodle.kfx.bindings.*
import de.flapdoodle.kfx.nodeeditor.types.NodeId
import de.flapdoodle.kfx.nodeeditor.types.NodeSlotId
import de.flapdoodle.kfx.nodeeditor.types.SlotId
import de.flapdoodle.kfx.types.AngleAtPoint2D
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.ReadOnlyMapWrapper
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableMap
import javafx.geometry.Point2D
import javafx.scene.transform.Transform

class NodeRegistry {
  private val nodes: ObservableMap<NodeId, Node> = FXCollections.observableHashMap()
  private val nodesPropery = ReadOnlyMapWrapper(nodes)
  private val nodeSlots: ObservableMap<NodeSlotId, ObservableValue<AngleAtPoint2D>> = FXCollections.observableHashMap()
  private val nodeSlotsPropery = ReadOnlyMapWrapper(nodeSlots)

  fun registerNode(node: Node) {
    nodes[node.nodeId] = node
  }

  fun unregisterNode(node: Node) {
    nodes.remove(node.nodeId)
  }

  fun registerConnection(connection: NodeConnection) {
    connection.init(this::scenePosition)
  }

  fun unregisterConnection(connection: NodeConnection) {
    connection.dispose()
  }

  private fun nodeByIdProperty(id: NodeId): ObjectBinding<Node?> {
    return nodesPropery.map { it[id] }
  }

  private fun scenePosition(nodeSlotId: NodeSlotId): ObjectBindings.DefaultIfNull<AngleAtPoint2D> {
    return ValueOfValueBinding.of(nodeSlotsPropery.map { it[nodeSlotId] }) { it }
      .defaultIfNull(Values.constantObject(AngleAtPoint2D(0.0, 0.0, 0.0)))
  }

  fun registerSlot(nodeSlotId: NodeSlotId, positionInScene: ObservableValue<AngleAtPoint2D>) {
    nodeSlots[nodeSlotId] = positionInScene
  }
}