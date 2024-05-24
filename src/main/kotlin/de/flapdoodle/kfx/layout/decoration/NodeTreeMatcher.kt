package de.flapdoodle.kfx.layout.decoration

import javafx.scene.Node
import javafx.scene.Parent

sealed class NodeTreeMatcher<C: Node> {
  data class SingleMatcher<C: Node>(
    val filter: NodeFilter<C>
  ): NodeTreeMatcher<C>()

  data class ChildMatcher<P: Parent, C: Node>(
    val filter: NodeFilter<P>,
    val child: NodeTreeMatcher<C>
  ): NodeTreeMatcher<C>()

  companion object {
    fun <C: Node> match(filter: NodeFilter<C>) = SingleMatcher(filter)
    fun <P: Parent, C: Node>match(filter: NodeFilter<P>, matcher: NodeTreeMatcher<C>) = ChildMatcher(filter, matcher)

    fun <P: Parent, C: Node> SingleMatcher<P>.and(filter: NodeFilter<C>): NodeTreeMatcher<C> {
      return ChildMatcher(this.filter, SingleMatcher(filter))
    }
  }
}