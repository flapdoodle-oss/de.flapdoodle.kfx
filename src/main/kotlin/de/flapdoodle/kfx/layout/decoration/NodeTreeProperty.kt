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

class NodeTreeProperty<P: Parent, C: Node>(
  val node: P,
  matcher: NodeTreeMatcher<C>
): ObservableValue<C> {

  private val listener = adapterOf(matcher)
  private val child = listener.valueProperty()

  init {
    listener.onAttach(node.childrenUnmodifiable)
    node.childrenUnmodifiable.addListener(listener)
  }

  override fun addListener(listener: ChangeListener<in C>) = child.addListener(listener)
  override fun addListener(listener: InvalidationListener) = child.addListener(listener)
  override fun removeListener(listener: InvalidationListener) = child.removeListener(listener)
  override fun removeListener(listener: ChangeListener<in C>) = child.removeListener(listener)
  override fun getValue(): C? = child.value

  private sealed class MatcherAdapter<C: Node>: ListChangeListener<Node> {
    abstract fun valueProperty(): ObservableValue<C>
    abstract fun onAttach(nodes: List<Node>)
    abstract fun onDetach()

    data class SingleMatcherAdapter<C: Node>(val filter: NodeFilter<C>): MatcherAdapter<C>() {
      private val valueProperty = SimpleObjectProperty<C>(null)
      init {
        if (debug) valueProperty.addListener { observable, oldValue, newValue ->
          println("$filter: $newValue")
        }
      }

      override fun valueProperty(): ObservableValue<C> = valueProperty

      override fun onAttach(nodes: List<Node>) {
        valueProperty.value = filter.filter(nodes)
      }

      override fun onChanged(c: ListChangeListener.Change<out Node>) {
        valueProperty.value = filter.filter(c.list)
      }

      override fun onDetach() {}
    }

    data class ChildMatcherAdapter<P: Parent, C: Node>(
      val filter: NodeFilter<P>,
      val child: MatcherAdapter<C>
    ): MatcherAdapter<C>() {
      private val parentProperty = SimpleObjectProperty<P>(null)
      init {
        if (debug) parentProperty.addListener { observable, oldValue, newValue ->
          println("$filter: $newValue")
        }
      }

      override fun valueProperty(): ObservableValue<C> = child.valueProperty()

      override fun onAttach(nodes: List<Node>) {
        parentProperty.value = filter.filter(nodes)
        parentProperty.value?.let {
          it.childrenUnmodifiable.addListener(child)
          child.onAttach(it.childrenUnmodifiable)
        }
      }
      override fun onChanged(c: ListChangeListener.Change<out Node>) {
        val old = parentProperty.value
        val new = filter.filter(c.list)
        if (old!=new) {
          old?.let {
            child.onDetach()
            it.childrenUnmodifiable.removeListener(child)
          }
          new?.let {
            it.childrenUnmodifiable.addListener(child)
            child.onAttach(it.childrenUnmodifiable)
          }
        }
        parentProperty.value = new
      }

      override fun onDetach() {
        child.onDetach()
        parentProperty.value?.let {
          it.childrenUnmodifiable.removeListener(this)
        }
      }
    }
  }

  companion object {
    private val debug = false

    private fun <T: Node> adapterOf(matcher: NodeTreeMatcher<T>): MatcherAdapter<T> {
      return when (matcher) {
        is NodeTreeMatcher.SingleMatcher -> MatcherAdapter.SingleMatcherAdapter(matcher.filter)
        is NodeTreeMatcher.ChildMatcher<out Parent, T> -> MatcherAdapter.ChildMatcherAdapter(matcher.filter, adapterOf(matcher.child))
      }
    }
  }
}