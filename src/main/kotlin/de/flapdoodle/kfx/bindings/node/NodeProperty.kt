package de.flapdoodle.kfx.bindings.node

import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.Node

class NodeProperty<C : Node, T : Any>(
  node: ObservableValue<out C?>,
  property: (C) -> ObservableValue<T?>
): ObservableValue<T?> {

  private val propertyListener = PropertyListener<T>()

  init {
    node.addListener { _, old, new ->
      if (old != null) {
        propertyListener.onDetach()
        property(old).removeListener(propertyListener)
      }
      if (new !=null) {
        property(new).addListener(propertyListener)
        propertyListener.onAttach(property(new).value)
      }
    }
    propertyListener.onAttach(node.value?.let { property(it).value })
  }

  override fun addListener(listener: ChangeListener<in T?>) = propertyListener.valueProperty.addListener(listener)
  override fun removeListener(listener: ChangeListener<in T?>) = propertyListener.valueProperty.addListener(listener)
  override fun addListener(listener: InvalidationListener?) = propertyListener.valueProperty.addListener(listener)
  override fun removeListener(listener: InvalidationListener?) = propertyListener.valueProperty.addListener(listener)
  override fun getValue(): T? = propertyListener.valueProperty.value

  class PropertyListener<T: Any>: ChangeListener<T?> {

    internal val valueProperty = SimpleObjectProperty<T>(null)

    fun onAttach(value: T?) {
      valueProperty.value = value
    }

    override fun changed(observable: ObservableValue<out T>, oldValue: T?, newValue: T?) {
      valueProperty.value = newValue
    }

    fun onDetach() {
      valueProperty.value = null
    }

  }
}