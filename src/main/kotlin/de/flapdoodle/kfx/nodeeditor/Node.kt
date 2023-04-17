package de.flapdoodle.kfx.nodeeditor

import de.flapdoodle.kfx.bindings.Bindings
import de.flapdoodle.kfx.bindings.NodeContainerProperty
import de.flapdoodle.kfx.extensions.layoutPosition
import de.flapdoodle.kfx.layout.backgrounds.Bounds
import de.flapdoodle.kfx.types.LayoutBounds
import javafx.css.PseudoClass
import javafx.geometry.Insets
import javafx.geometry.Point2D
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.scene.paint.Color
import java.util.UUID

class Node(val name: String) : BorderPane() {
  val uuid=UUID.randomUUID()

  companion object {
    fun onlyNodes(node: javafx.scene.Node): List<Node> {
      return if (node is Parent) {
        node.childrenUnmodifiable.filterIsInstance<Node>()
      } else {
        emptyList<Node>()
      }
    }
  }

  object Style {
    val Active: PseudoClass = PseudoClass.getPseudoClass("active")

    fun PseudoClass.enable(destination: Node) {
      destination.pseudoClassStateChanged(this, true)
    }

    fun PseudoClass.disable(destination: Node) {
      destination.pseudoClassStateChanged(this, false)
    }
  }

  private val contentWrapper=StackPane()
  private val _content=NodeContainerProperty.of<javafx.scene.Node>("content", contentWrapper::getChildren)

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

  fun someFakeHandleCoord(): Bindings.Merge2<Number, Number, Point2D> {
    return Bindings.map(layoutXProperty(), layoutYProperty()) { x, y ->
      Point2D(x.toDouble()-5.0,y.toDouble() + 10.0)
    }
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
}