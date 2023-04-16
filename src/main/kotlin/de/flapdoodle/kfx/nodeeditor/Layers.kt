package de.flapdoodle.kfx.nodeeditor

import de.flapdoodle.kfx.bindings.Bindings
import de.flapdoodle.kfx.extensions.BoundingBoxes
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.collections.ObservableList
import javafx.geometry.Bounds
import javafx.scene.Node
import javafx.scene.layout.Region
import javafx.scene.shape.Rectangle
import javafx.scene.transform.Transform

class Layers : Region() {
  private val nodes = Layer()
  private val connections = Layer()

  init {
    isManaged = false
    width = 10.0
    height = 10.0

    children.add(nodes)
    children.add(connections)
  }

  fun nodes() = nodes
  fun connections() = connections

  fun boundingBoxProperty(): ObjectBinding<Bounds> {
    return BoundingBoxes.mapLocalToParent(this, nodes.boundingBoxProperty())
  }

  class Layer : Region() {

    public override fun getChildren(): ObservableList<Node> {
      return super.getChildren()
    }

    fun boundingBox(): Rectangle {
      return Nodes.childBoundsRectangle(this)
    }

    fun boundingBoxProperty(): ReadOnlyObjectProperty<Bounds> {
      return BoundingBoxes.boundsInParentProperty(this) { node -> node is de.flapdoodle.kfx.nodeeditor.Node}
    }
  }
}