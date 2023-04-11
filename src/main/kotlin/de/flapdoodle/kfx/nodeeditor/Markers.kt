package de.flapdoodle.kfx.nodeeditor

import de.flapdoodle.kfx.extensions.Key
import de.flapdoodle.kfx.extensions.constraint
import javafx.scene.Node

object Markers {
  val IsDragBar = Key.ofType(Boolean::class)

  fun isDragBar(node: Node): Boolean {
    return node.constraint[IsDragBar] ?: false
  }

  fun markAsDragBar(node: Node) {
    node.constraint[IsDragBar] = true
  }

  fun unmarkAsDragBar(node: Node) {
    node.constraint[IsDragBar] = null
  }
}