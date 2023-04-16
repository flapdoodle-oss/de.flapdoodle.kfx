package de.flapdoodle.kfx.nodeeditor

import de.flapdoodle.kfx.extensions.BoundingBoxes
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.collections.ObservableList
import javafx.geometry.Bounds
import javafx.scene.Node
import javafx.scene.layout.Region

class Layers : Region() {
  private val nodes = Layer(de.flapdoodle.kfx.nodeeditor.Node::class.java)
  private val connections = Layer(Node::class.java)
  private val hints = Layer(Node::class.java)

  init {
    isManaged = false
    width = 10.0
    height = 10.0

    children.add(hints)
    children.add(nodes)
    children.add(connections)
  }

  fun nodes() = nodes
  fun connections() = connections
  fun hints() = hints

  fun boundingBoxProperty(): ObjectBinding<Bounds> {
    return BoundingBoxes.mapLocalToParent(this, nodes.boundingBoxProperty())
  }

  class Layer<T: Node>(val type: Class<T>) : Region() {

    public override fun getChildren(): ObservableList<Node> {
      return super.getChildren()
    }

    fun add(vararg nodes: T) {
      children.addAll(nodes)
    }

    fun boundingBoxProperty(): ReadOnlyObjectProperty<Bounds> {
      return BoundingBoxes.boundsInParentProperty(this, type::isInstance)
    }
  }
}