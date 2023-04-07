package de.flapdoodle.kfx.nodeeditor

import javafx.css.PseudoClass
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.effect.DropShadow
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class Node(val name: String) : Pane(), IsNode {
  init {
    styleClass.addAll("node")
    stylesheets += javaClass.getResource("Node.css").toExternalForm()

    children.add(BorderPane().apply {
      center = Label(name)
      bottom = Button("...")
      padding = Insets(10.0)
    })
//    border= Border(BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii(3.0), BorderWidths.DEFAULT))
    children.add(Rectangle(30.0, 10.0).apply {
      isManaged = false
      fill = Color.RED.brighter()
      border= Border(BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, CornerRadii(3.0), BorderWidths.DEFAULT))
      relocate(-10.0, -10.0)
    })

    // default styling
//    val shadow = DropShadow(10.0, Color.DARKGRAY)
//    effect = shadow
  }

  override fun onFocus() {
//    border= Border(BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, CornerRadii(3.0), BorderWidths.DEFAULT))
//    styleClass.addAll("node-focused")
    pseudoClassStateChanged(PseudoClass.getPseudoClass("active"), true)
  }

  override fun onBlur() {
//    border= Border(BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii(3.0), BorderWidths.DEFAULT))
//    styleClass.removeAll("node-focused")
    pseudoClassStateChanged(PseudoClass.getPseudoClass("active"), false)
  }

  fun resizeTo(width: Double, height: Double) {
    val pW = computePrefWidth(width)
    val pH = computePrefHeight(height)
    this.prefWidth = width.coerceAtLeast(pW)
    this.prefHeight = height.coerceAtLeast(pH)
  }
}