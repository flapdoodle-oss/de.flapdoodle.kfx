package de.flapdoodle.kfx.nodeeditor

import de.flapdoodle.kfx.bindings.NodeContainerProperty
import de.flapdoodle.kfx.bindings.ObjectBindings
import de.flapdoodle.kfx.bindings.and
import de.flapdoodle.kfx.extensions.PseudoClassWrapper
import de.flapdoodle.kfx.extensions.layoutPosition
import de.flapdoodle.kfx.nodeeditor.connectors.ConnectorsPane
import de.flapdoodle.kfx.nodeeditor.model.Position
import de.flapdoodle.kfx.nodeeditor.model.Slot
import de.flapdoodle.kfx.nodeeditor.types.NodeId
import de.flapdoodle.kfx.nodeeditor.types.SlotId
import de.flapdoodle.kfx.types.AngleAtPoint2D
import de.flapdoodle.kfx.types.LayoutBounds
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.css.PseudoClass
import javafx.geometry.Insets
import javafx.geometry.Point2D
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.scene.paint.Color

class Node(val name: String) : BorderPane() {
  val nodeId=NodeId()
  val registry = SimpleObjectProperty<NodeRegistry>()

  companion object {
    fun onlyNodes(node: javafx.scene.Node): List<Node> {
      return if (node is Parent) {
        node.childrenUnmodifiable.filterIsInstance<Node>()
      } else {
        emptyList<Node>()
      }
    }
  }

  private object Style {
    val Active = PseudoClassWrapper<Node>(PseudoClass.getPseudoClass("active"))
  }

  private val contentWrapper=StackPane()
  private val _content=NodeContainerProperty.of<javafx.scene.Node>("content", contentWrapper::getChildren)
  private val connectors = FXCollections.observableArrayList<Slot>()

  var content: javafx.scene.Node
    get() = _content.get()
    set(value) { _content.set(value)}

  init {
    styleClass.addAll("node")
    stylesheets += javaClass.getResource("Node.css").toExternalForm()

    setMargin(contentWrapper, Insets(10.0))

    center = contentWrapper
    top = NodeHeader(name).apply {
      Markers.markAsDragBar(this)
    }
    left = ConnectorsPane(registry, nodeId, connectors, Position.LEFT)
    right = ConnectorsPane(registry, nodeId, connectors, Position.RIGHT)
    bottom = ConnectorsPane(registry, nodeId, connectors, Position.BOTTOM)
  }

  fun resizeTo(bounds: LayoutBounds) {
    val width = bounds.size.width
    val height = bounds.size.height

    val pW = computePrefWidth(width)
    val pH = computePrefHeight(height)

    prefWidth = width.coerceAtLeast(pW)
    prefHeight = height.coerceAtLeast(pH)
    layoutPosition = bounds.layoutPosition
  }

  fun someFakeHandleCoord(): ObjectBinding<Point2D> {
    return ObjectBindings.merge(layoutXProperty(), layoutYProperty()){ x, y ->
      Point2D(x.toDouble()-5.0,y.toDouble() + 10.0)
    }
  }

  fun someFakeConnector(): ObjectBinding<AngleAtPoint2D> {
    return layoutXProperty().and(layoutYProperty()).map { x, y ->
      AngleAtPoint2D(Point2D(x.toDouble()-5.0, y.toDouble() + 20.0), 0.0)
    }
  }

  fun addConnector(connector: Slot) {
    connectors.add(connector)
  }

  fun removeConnector(slotId: SlotId) {
    connectors.removeIf { it.id==slotId }
  }

  class NodeHeader(label: String) : HBox() {
    init {
      isMouseTransparent = false
      background = Background(BackgroundFill(Color.GREY, CornerRadii(2.0), Insets(1.0)))
      children.add(Label(label).apply {
        setHgrow(this, Priority.ALWAYS)
      })
    }
  }

  fun focus() {
    Style.Active.enable(this)
  }

  fun blur() {
    Style.Active.disable(this)
  }

}