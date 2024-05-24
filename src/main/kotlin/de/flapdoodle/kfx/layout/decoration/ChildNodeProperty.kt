package de.flapdoodle.kfx.layout.decoration

import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.scene.Node
import javafx.scene.Parent

class ChildNodeProperty<P: Parent, C: Node>(
  val node: P,
  val filter: Filter<C>
): ObservableValue<C>  {
  private val child = SimpleObjectProperty<C>(filter.find(node.childrenUnmodifiable))

  init {
    node.childrenUnmodifiable.addListener(ListChangeListener { change ->
      child.value = filter.find(change.list)
    })
  }

  override fun addListener(listener: ChangeListener<in C>) = child.addListener(listener)
  override fun addListener(listener: InvalidationListener) = child.addListener(listener)
  override fun removeListener(listener: InvalidationListener?) = child.removeListener(listener)
  override fun removeListener(listener: ChangeListener<in C>?) = child.removeListener(listener)
  override fun getValue(): C? = child.value

  fun interface Filter<C: Node> {
    fun find(nodes: List<Node>): C?
  }
}