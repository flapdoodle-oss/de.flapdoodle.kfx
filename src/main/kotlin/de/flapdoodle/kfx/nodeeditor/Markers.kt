package de.flapdoodle.kfx.nodeeditor

import de.flapdoodle.kfx.extensions.Key
import de.flapdoodle.kfx.extensions.constraint
import de.flapdoodle.kfx.nodeeditor.types.ConnectionId
import de.flapdoodle.kfx.nodeeditor.types.NodeSlotId
import javafx.scene.Node

object Markers {
  val IsDragBar = Key.ofType(Boolean::class)
  val nodeSlot = Key.ofType(NodeSlotId::class)
  val connection = Key.ofType(ConnectionId::class)

  fun isDragBar(node: Node): Boolean {
    return node.constraint[IsDragBar] ?: false
  }

  fun markAsDragBar(node: Node) {
    node.constraint[IsDragBar] = true
  }

  fun unmarkAsDragBar(node: Node) {
    node.constraint[IsDragBar] = null
  }

  fun connection(node: Node): ConnectionId? {
    return node.constraint[connection]
  }

  fun markAsConnection(node: Node, connectionId: ConnectionId) {
    node.constraint[connection] = connectionId
  }

  fun unmarkAsConnection(node: Node) {
    node.constraint[connection] = null
  }

  fun nodeSlot(node: Node): NodeSlotId? {
    return node.constraint[nodeSlot]
  }

  fun markAsNodeSlot(node: Node, id: NodeSlotId) {
    node.constraint[nodeSlot] = id
  }

  fun unmarkAsNodeSlot(node: Node) {
    node.constraint[nodeSlot] = null
  }


}