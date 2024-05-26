package de.flapdoodle.kfx.bindings.node

import javafx.beans.InvalidationListener
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.Node

class NodeProperties<C : Node, T : Any>(
  node: ObservableValue<out C?>,
  binding: (C) -> ObjectBinding<T?>
): ObservableValue<T?> {

  private val valueProperty = SimpleObjectProperty<T>(null)

  init {
    node.addListener { _, old, new ->
      if (old != null) {
        valueProperty.unbind()
      }
      if (new !=null) {
        valueProperty.bind(binding(new))
      } else {
        valueProperty.value = null
      }
    }
    node.value?.let { valueProperty.bind(binding(it)) }
  }

  override fun addListener(listener: ChangeListener<in T?>) = valueProperty.addListener(listener)
  override fun removeListener(listener: ChangeListener<in T?>) = valueProperty.removeListener(listener)

  override fun addListener(listener: InvalidationListener?) = valueProperty.addListener(listener)
  override fun removeListener(listener: InvalidationListener?) = valueProperty.removeListener(listener)

  override fun getValue(): T? = valueProperty.value
}