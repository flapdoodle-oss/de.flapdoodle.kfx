package de.flapdoodle.kfx.nodeeditor

import de.flapdoodle.kfx.bindings.and
import de.flapdoodle.kfx.extensions.BoundingBoxes
import de.flapdoodle.kfx.nodeeditor.events.NodeConnectionEvent
import de.flapdoodle.kfx.nodeeditor.events.NodeEvent
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.event.Event
import javafx.geometry.Bounds
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.Region

class Layers : Region() {
  private val nodesBoundsMapping = BoundingBoxes.BoundMapping(de.flapdoodle.kfx.nodeeditor.Node::onlyNodes, Node::getBoundsInParent)
  private val connectionBoundsMapping = BoundingBoxes.BoundMapping(NodeConnection::onlyConnections, NodeConnection::boundsInParent)
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
  }

  // TODO remove??
  fun nodes() = nodes
  fun connections() = connections
  fun hints() = hints

  fun boundingBoxProperty(): ObservableValue<Bounds> {
    return nodes.boundingBoxProperty()
      .and(connections.boundingBoxProperty())
      .map(BoundingBoxes::merge)
  }

  fun addNodes(vararg list: de.flapdoodle.kfx.nodeeditor.Node) {
    nodes.add(*list)
    list.forEach {
      fireEvent(NodeEvent(NodeEvent.NODE_ADDED, it))
    }
  }

  fun addConnections(vararg list: NodeConnection) {
    connections.add(*list)
    list.forEach {
      fireEvent(NodeConnectionEvent(NodeConnectionEvent.ADDED, it))
    }
  }

  fun addHints(vararg list: Node) {
    hints.add(*list)
  }

  class Layer<T: Node>(val type: Class<T>, private val boundMapping: BoundingBoxes.BoundMapping<T>) : Region() {

    public override fun getChildren(): ObservableList<Node> {
      return super.getChildren()
    }

    internal fun add(vararg nodes: T) {
      children.addAll(nodes)
    }

    fun boundingBoxProperty(): ReadOnlyObjectProperty<Bounds> {
      return BoundingBoxes.reduceBoundsProperty(this, boundMapping)
//      return BoundingBoxes.boundsInParentProperty(this, type::isInstance)
    }
  }
}