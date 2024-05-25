package de.flapdoodle.kfx.bindings.node

import javafx.scene.Node
import kotlin.reflect.KClass

fun interface ChildNodeFilter<C : Node> {
  fun filter(nodes: List<Node>): C?

  fun and(other: ChildNodeFilter<C>): ChildNodeFilter<C> {
    val that = this
    return ChildNodeFilter<C> { list ->
      val result = that.filter(list)
      result?.let { other.filter(listOf(it)) }
    }
  }

  companion object {
    fun <C : Node> isInstance(nodeType: KClass<C>): ChildNodeFilter<C> {
      return isInstance(nodeType.java)
    }

    fun <C : Node> isInstance(nodeType: Class<C>): ChildNodeFilter<C> {
      return object : ChildNodeFilter<C> {
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