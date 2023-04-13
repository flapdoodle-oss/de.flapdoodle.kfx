package de.flapdoodle.kfx.nodeeditor

import de.flapdoodle.kfx.bindings.NodeContainerProperty
import de.flapdoodle.kfx.extensions.layoutPosition
import de.flapdoodle.kfx.extensions.withAnchors
import de.flapdoodle.kfx.types.LayoutBounds
import javafx.css.PseudoClass
import javafx.geometry.Dimension2D
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class Node(val name: String) : BorderPane() {

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
//    top = Label(name).apply {
//      Markers.markAsDragBar(this)
////      setMargin(this, Insets(10.0))
//      BorderPane.setAlignment(this, Pos.CENTER)
//    }


//    children.add(BorderPane().apply {
//      top = Label(name).apply {
//        Markers.markAsDragBar(this)
//      }
//      center = contentWrapper
//      bottom = Button("...")
//      padding = Insets(10.0)
//    })
////    border= Border(BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii(3.0), BorderWidths.DEFAULT))
//    children.add(Rectangle(30.0, 10.0).apply {
//      isManaged = false
//      fill = Color.RED.brighter()
//      border = Border(BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, CornerRadii(3.0), BorderWidths.DEFAULT))
//      relocate(-10.0, -10.0)
//    })

    // default styling
//    val shadow = DropShadow(10.0, Color.DARKGRAY)
//    effect = shadow
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

  class NodeHeader(label: String) : HBox() {
    init {
      isMouseTransparent = false
      background = Background(BackgroundFill(Color.GREY, null, null))
      children.add(Label(label).apply {
        setHgrow(this, Priority.ALWAYS)
      })
    }
  }
}