package de.flapdoodle.kfx.nodeeditor

import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.layout.Region
import javafx.scene.shape.Rectangle

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

  class Layer : Region() {

    public override fun getChildren(): ObservableList<Node> {
      return super.getChildren()
    }

    fun boundingBox(): Rectangle {
      return Nodes.childBoundsRectangle(this)
    }
  }
}