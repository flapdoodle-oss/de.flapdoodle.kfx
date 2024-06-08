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

import javafx.collections.ListChangeListener
import javafx.scene.Node
import javafx.scene.Parent

class TreeNodeProperty<P: Parent, C: Node>(
  val node: P,
  val filter: FilterListChangeListener<C>
) {

  init {
    filter.onAttach(node.childrenUnmodifiable)
    node.childrenUnmodifiable.addListener(filter)
  }

  abstract class FilterListChangeListener<C: Node> : ListChangeListener<Node> {
    internal abstract fun onAttach(list: List<Node>)
    internal abstract fun onDetach()
  }

  class NodeOfNode<P: Parent, C: Node>(
    private val filter: (List<Node>) -> P?,
    private val child: FilterListChangeListener<C>
  ) : FilterListChangeListener<C>() {
    private var _filtered: P? = null

    private var filtered: P?
      set(value) {
        _filtered?.let {
          it.childrenUnmodifiable.removeListener(child)
        }
        _filtered = value
        _filtered?.let {
          child.onAttach(it.childrenUnmodifiable)
          it.childrenUnmodifiable.addListener(child)
        }
      }
      get() { return _filtered }

    override fun onAttach(list: List<Node>) {
      filtered = filter(list)
    }

    override fun onDetach() {
      child.onDetach()
    }

    override fun onChanged(c: ListChangeListener.Change<out Node>) {
      filtered = filter(c.list)
    }

    fun <T: Parent> inside(filter: (List<Node>) -> T?): NodeOfNode<T, C> {
      return NodeOfNode(filter, this)
    }
  }

  class SingleNode<C: Node>(
    private val filter: (List<Node>) -> C?
  ): FilterListChangeListener<C>() {
    private var _filtered: C? = null

    private var filtered: C?
      set(value) {
        _filtered = value
        _filtered?.let {
          println("found --> $it")
        }
      }
      get() { return _filtered }

    override fun onAttach(list: List<Node>) {
      filtered = filter(list)
    }

    override fun onChanged(c: ListChangeListener.Change<out Node>) {
      filtered = filter(c.list)
    }

    override fun onDetach() {
      
    }

    fun <P: Parent> inside(filter: (List<Node>) -> P?): NodeOfNode<P, C> {
      return NodeOfNode(filter, this)
    }
  }

  companion object {
    fun <C: Node> filter(filter: (List<Node>) -> C?): SingleNode<C> {
      return SingleNode(filter)
    }
  }
}