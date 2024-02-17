package de.flapdoodle.kfx.controls.grapheditor

import de.flapdoodle.kfx.controls.grapheditor.types.EdgeId
import de.flapdoodle.kfx.controls.grapheditor.types.VertexSlotId
import de.flapdoodle.kfx.extensions.Key
import de.flapdoodle.kfx.extensions.constraint
import javafx.scene.Node

object Markers {
  val IsDragBar = Key.ofType(Boolean::class)
  val nodeSlot = Key.ofType(VertexSlotId::class)
  val connection = Key.ofType(EdgeId::class)

  fun isDragBar(node: Node): Boolean {
    return node.constraint[IsDragBar] ?: false
  }

  fun markAsDragBar(node: Node) {
    node.constraint[IsDragBar] = true
  }

  fun unmarkAsDragBar(node: Node) {
    node.constraint[IsDragBar] = null
  }

  fun connection(node: Node): EdgeId? {
    return node.constraint[connection]
  }

  fun markAsConnection(node: Node, edgeId: EdgeId) {
    node.constraint[connection] = edgeId
  }

  fun unmarkAsConnection(node: Node) {
    node.constraint[connection] = null
  }

  fun nodeSlot(node: Node): VertexSlotId? {
    return node.constraint[nodeSlot]
  }

  fun markAsNodeSlot(node: Node, id: VertexSlotId) {
    node.constraint[nodeSlot] = id
  }

  fun unmarkAsNodeSlot(node: Node) {
    node.constraint[nodeSlot] = null
  }


}