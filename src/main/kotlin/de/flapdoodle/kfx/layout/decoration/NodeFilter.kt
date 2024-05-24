package de.flapdoodle.kfx.layout.decoration

import javafx.scene.Node
import kotlin.reflect.KClass

fun interface NodeFilter<C : Node> {
  fun filter(nodes: List<Node>): C?

  fun and(other: NodeFilter<C>): NodeFilter<C> {
    val that = this
    return NodeFilter<C> { list ->
      val result = that.filter(list)
      result?.let { other.filter(listOf(it)) }
    }
  }

  companion object {
    fun <C : Node> isInstance(nodeType: KClass<C>): NodeFilter<C> {
      return isInstance(nodeType.java)
    }

    fun <C : Node> isInstance(nodeType: Class<C>): NodeFilter<C> {
      return object : NodeFilter<C> {
        override fun filter(list: List<Node>): C? {
          return list.filterIsInstance(nodeType).firstOrNull()
        }

        override fun toString(): String {
          return "IsInstanceOf($nodeType)"
        }
      }
    }
  }
}