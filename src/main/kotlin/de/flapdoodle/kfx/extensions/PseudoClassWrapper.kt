package de.flapdoodle.kfx.extensions

import javafx.css.PseudoClass
import javafx.scene.Node

class PseudoClassWrapper<T: Node>(
  private val wrapped: PseudoClass
) {
  fun enable(node: T) {
    node.pseudoClassStateChanged(wrapped, true)
  }

  fun disable(node: T) {
    node.pseudoClassStateChanged(wrapped, false)
  }

  fun swap(node: T) {
    node.pseudoClassStateChanged(wrapped, !node.pseudoClassStates.contains(wrapped))
  }
}