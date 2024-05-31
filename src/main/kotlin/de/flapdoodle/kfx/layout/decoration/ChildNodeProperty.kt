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