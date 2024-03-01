package de.flapdoodle.kfx.controls.grapheditor

import de.flapdoodle.kfx.controls.grapheditor.types.VertexSlotId
import de.flapdoodle.kfx.extensions.Key
import de.flapdoodle.kfx.extensions.constraint
import javafx.scene.Node

object Markers {
  private val IsDragBar = Key.ofType(Boolean::class)
  private val nodeSlot = Key.ofType(VertexSlotId::class)
  private val IsContent = Key.ofNamedType("isContent", Boolean::class)

  fun isDragBar(node: Node): Boolean {
    return node.constraint[IsDragBar] ?: false
  }

  fun markAsDragBar(node: Node) {
    node.constraint[IsDragBar] = true
  }

  fun unmarkAsDragBar(node: Node) {
    node.constraint[IsDragBar] = null
  }

  fun isContent(node: Node): Boolean {
    return node.constraint[IsContent] ?: false
  }

  fun markAsContent(node: Node) {
    node.constraint[IsContent] = true
  }

  fun unmarkAsContent(node: Node) {
    node.constraint[IsContent] = null
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