package de.flapdoodle.kfx.bindings

import javafx.beans.property.ObjectPropertyBase
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.Node

abstract class NodeContainerProperty<T: Node>(private val name: String): ObjectPropertyBase<T>() {
  private var oldValue: Node? = null
  private var isBeingInvalidated = false

  init {
    getChildren().addListener(ListChangeListener { c ->
      if (oldValue == null || isBeingInvalidated) {
        return@ListChangeListener
      }
      while (c.next()) {
        if (c.wasRemoved()) {
          val removed = c.removed
          var i = 0
          val sz = removed.size
          while (i < sz) {
            if (removed[i] === oldValue) {
              oldValue = null // Do not remove again in invalidated
              set(null)
            }
            ++i
          }
        }
      }
    })
  }

  override fun invalidated() {
    val children: MutableList<Node> = getChildren()
    isBeingInvalidated = true
    try {
      if (oldValue != null) {
        children.remove(oldValue)
      }
      val _value = get()
      oldValue = _value
      if (_value != null) {
        children.add(_value)
      }
    } finally {
      isBeingInvalidated = false
    }
  }

  override fun getBean(): Any {
    return this
  }

  override fun getName(): String {
    return name
  }
  
  abstract fun getChildren(): ObservableList<Node>

  companion object {
    fun <T: Node> of(name: String, children: () -> ObservableList<Node>): NodeContainerProperty<T> {
      return object : NodeContainerProperty<T>(name) {
        override fun getChildren(): ObservableList<Node> {
          return children()
        }
      }
    }
  }
}
