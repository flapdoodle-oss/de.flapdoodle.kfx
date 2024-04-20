/*
 * Copyright (C) 2022
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
