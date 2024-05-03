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
package de.flapdoodle.kfx.controls.grapheditor

import de.flapdoodle.kfx.bindings.and
import de.flapdoodle.kfx.bindings.storeTo
import de.flapdoodle.kfx.types.BoundingBoxes
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.Region

class Layers(private val registry: Registry) : Region() {
  private val nodesBoundsMapping = BoundingBoxes.BoundMapping(Vertex::onlyNodes, Node::getBoundsInParent)
  private val edgeBoundsMapping = BoundingBoxes.BoundMapping(Edge::onlyConnections, Edge::boundsInParent)
  private val nativeNodeBoundsMapping = BoundingBoxes.BoundMapping<Node>({ if (it is Parent) it.childrenUnmodifiable else emptyList() }, Node::getBoundsInParent)

  private val background = Background()
  private val nodes = Layer(Vertex::class.java, nodesBoundsMapping)
  private val connections = Layer(Edge::class.java, edgeBoundsMapping)
  private val hints = Layer(Node::class.java, nativeNodeBoundsMapping)

  private val visibleArea = SimpleObjectProperty<Bounds>(BoundingBox(-50.0, -50.0, 100.0, 100.0))

  init {
    isManaged = false
    width = 10.0
    height = 10.0

    children.add(background)
    children.add(connections)
    children.add(nodes)
    children.add(hints)

    clip = de.flapdoodle.kfx.layout.backgrounds.Bounds.sizeRectangle(visibleArea)
//    background.area().bindTo(visibleArea)
    visibleArea.storeTo(background.area())
  }

  // TODO remove??
  fun nodes() = nodes
  fun background() = background
  fun visibleArea(): ObjectProperty<Bounds> = visibleArea

  fun boundingBoxProperty(): ObservableValue<Bounds> {
    return nodes.boundingBoxProperty()
      .and(connections.boundingBoxProperty())
      .map(BoundingBoxes::merge)
  }

  fun addVertex(vararg list: Vertex) {
    nodes.add(*list)
    list.forEach {
      it.registry.value = registry
      registry.registerNode(it)
    }
  }

  fun addEdge(vararg list: Edge) {
    connections.add(*list)
    list.forEach {
      it.registry.value = registry
      registry.registerConnection(it)
    }
  }

  fun vertices(): List<Vertex> {
    return nodes.children.filterIsInstance(Vertex::class.java)
  }

  fun edges(): List<Edge> {
    return connections.children.filterIsInstance(Edge::class.java)
  }

  fun removeVertex(list: List<Vertex>) {
    nodes.remove(*list.toTypedArray())
    list.forEach {
      registry.unregisterNode(it)
    }
  }

  fun removeEdges(list: List<Edge>) {
    connections.remove(*list.toTypedArray())
    list.forEach {
      registry.unregisterConnection(it)
    }
  }

  fun addHints(vararg list: Node) {
    hints.add(*list)
  }

  fun removeHints(vararg list: Node) {
    hints.remove(*list)
  }

  class Layer<T: Node>(val type: Class<T>, private val boundMapping: BoundingBoxes.BoundMapping<T>) : Region() {

    init {
      isPickOnBounds = false
    }

    public override fun getChildren(): ObservableList<Node> {
      return super.getChildren()
    }

    internal fun add(vararg nodes: T) {
      children.addAll(nodes)
    }

    internal fun remove(vararg nodes: T) {
      children.removeAll(nodes)
    }

    fun boundingBoxProperty(): ReadOnlyObjectProperty<Bounds> {
      return BoundingBoxes.reduceBoundsProperty(this, boundMapping)
//      return BoundingBoxes.boundsInParentProperty(this, type::isInstance)
    }

    @Deprecated("don't use")
    fun forEach(action: (T) -> Unit) {
      children.forEach { node -> action(type.cast(node))}
    }
  }
}