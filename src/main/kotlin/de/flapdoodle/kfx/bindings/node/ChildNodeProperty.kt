package de.flapdoodle.kfx.bindings.node

import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.scene.Node
import javafx.scene.Parent

class ChildNodeProperty<P: Parent, C: Node>(
  parent: ObservableValue<out P?>,
  filter: ChildNodeFilter<C>
): ObservableValue<C> {

  private val childListener = ChildNodeListener(filter)
  private val child = childListener.child

  init {
    parent.addListener { _, old, new ->
      old?.let {
        childListener.onDetach()
        it.childrenUnmodifiable.removeListener(childListener)
      }
      new?.let {
        it.childrenUnmodifiable.addListener(childListener)
        childListener.onAttach(it.childrenUnmodifiable)
      }
    }
    parent.value?.let {
      it.childrenUnmodifiable.addListener(childListener)
      childListener.onAttach(it.childrenUnmodifiable)
    }
  }

  constructor(parent: P, filter: ChildNodeFilter<C>) : this(SimpleObjectProperty(parent), filter)

  override fun addListener(listener: ChangeListener<in C>) = child.addListener(listener)
  override fun addListener(listener: InvalidationListener) = child.addListener(listener)
  override fun removeListener(listener: InvalidationListener) = child.removeListener(listener)
  override fun removeListener(listener: ChangeListener<in C>) = child.removeListener(listener)
  override fun getValue(): C? = child.value

  fun <T: Any> property(property: (C) -> ObservableValue<T?>): NodeProperty<C, T> {
    return NodeProperty(this, property)
  }

  class ChildNodeListener<C: Node>(
    private val filter: ChildNodeFilter<C>
  ) : ListChangeListener<Node> {

    internal val child = SimpleObjectProperty<C>(null)

    fun onAttach(list: List<Node>) {
      child.value = filter.filter(list)
    }

    override fun onChanged(c: ListChangeListener.Change<out Node>) {
      child.value = filter.filter(c.list)
    }

    fun onDetach() {
      child.value = null
    }
  }

  companion object {
    fun <T: Parent, C: Node> ChildNodeProperty<out Parent, T>.andThen(filter: ChildNodeFilter<C>): ChildNodeProperty<T, C> {
      return ChildNodeProperty(this, filter)
    }
  }
}