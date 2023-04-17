package de.flapdoodle.kfx.nodeeditor

import de.flapdoodle.kfx.bindings.Bindings
import de.flapdoodle.kfx.extensions.BoundingBoxes
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.geometry.Bounds
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.Region

class Layers(val model: Model) : Region() {
  private val nodesBoundsMapping = BoundingBoxes.BoundMapping(de.flapdoodle.kfx.nodeeditor.Node::onlyNodes, Node::getBoundsInParent)
  private val connectionBoundsMapping = BoundingBoxes.BoundMapping(NodeConnection::onlyConnections, NodeConnection::lineBoundsInParent)
  private val hintsBoundsMapping = BoundingBoxes.BoundMapping<Node>({ if (it is Parent) it.childrenUnmodifiable else emptyList() }, Node::getBoundsInParent)

  private val nodes = Layer(de.flapdoodle.kfx.nodeeditor.Node::class.java, nodesBoundsMapping)
  private val connections = Layer(NodeConnection::class.java, connectionBoundsMapping)
  private val hints = Layer(Node::class.java, hintsBoundsMapping)

  init {
    isManaged = false
    width = 10.0
    height = 10.0

    children.add(hints)
    children.add(nodes)
    children.add(connections)

    nodes.children.addListener(ListChangeListener { change ->
      while (change.next()) {
        change.addedSubList
          .filterIsInstance<de.flapdoodle.kfx.nodeeditor.Node>()
          .forEach { n -> model.registerNode(n) }
        change.removed.filterIsInstance<de.flapdoodle.kfx.nodeeditor.Node>()
          .forEach { n -> model.unregisterNode(n) }
      }
    })

    connections.children.addListener(ListChangeListener { change ->
      while (change.next()) {
        change.addedSubList
          .filterIsInstance<NodeConnection>()
          .forEach { n -> model.registerConnection(n) }
        change.removed.filterIsInstance<NodeConnection>()
          .forEach { n -> model.registerConnection(n) }
      }
    })
  }

  fun nodes() = nodes
  fun connections() = connections
  fun hints() = hints

  fun boundingBoxProperty(): ObservableValue<Bounds> {
    return Bindings.map(nodes.boundingBoxProperty(), connections.boundingBoxProperty(), BoundingBoxes::merge)
  }

  class Layer<T: Node>(val type: Class<T>, val boundMapping: BoundingBoxes.BoundMapping<T>) : Region() {

    public override fun getChildren(): ObservableList<Node> {
      return super.getChildren()
    }

    fun add(vararg nodes: T) {
      children.addAll(nodes)
    }

    fun boundingBoxProperty(): ReadOnlyObjectProperty<Bounds> {
      return BoundingBoxes.reduceBoundsProperty(this, boundMapping)
//      return BoundingBoxes.boundsInParentProperty(this, type::isInstance)
    }
  }
}