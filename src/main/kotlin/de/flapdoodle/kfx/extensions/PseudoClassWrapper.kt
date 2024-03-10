package de.flapdoodle.kfx.extensions

import javafx.css.PseudoClass
import javafx.scene.Node

class PseudoClassWrapper<T: Node>(
  private val wrapped: PseudoClass
) {
  fun enable(node: T) {
    set(node, true)
  }

  fun enabled(node: T): Boolean {
    return node.pseudoClassStates.contains(wrapped)
  }

  fun disable(node: T) {
    set(node, false)
  }

  fun set(node: T, active: Boolean) {
    node.pseudoClassStateChanged(wrapped, active)
  }

  fun swap(node: T) {
    node.pseudoClassStateChanged(wrapped, !node.pseudoClassStates.contains(wrapped))
  }
}