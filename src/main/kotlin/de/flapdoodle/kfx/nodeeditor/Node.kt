package de.flapdoodle.kfx.nodeeditor

import de.flapdoodle.kfx.extensions.layoutPosition
import de.flapdoodle.kfx.types.LayoutBounds
import javafx.css.PseudoClass
import javafx.geometry.Dimension2D
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class Node(val name: String) : Pane() {

  object Style {
    val Active: PseudoClass = PseudoClass.getPseudoClass("active")

    fun PseudoClass.enable(destination: Node) {
      destination.pseudoClassStateChanged(this, true)
    }

    fun PseudoClass.disable(destination: Node) {
      destination.pseudoClassStateChanged(this, false)
    }
  }

  init {
    styleClass.addAll("node")
    stylesheets += javaClass.getResource("Node.css").toExternalForm()

    children.add(BorderPane().apply {
      center = Label(name).apply {
        Markers.markAsDragBar(this)
      }
      bottom = Button("...")
      padding = Insets(10.0)
    })
//    border= Border(BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii(3.0), BorderWidths.DEFAULT))
    children.add(Rectangle(30.0, 10.0).apply {
      isManaged = false
      fill = Color.RED.brighter()
      border = Border(BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, CornerRadii(3.0), BorderWidths.DEFAULT))
      relocate(-10.0, -10.0)
    })

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
}