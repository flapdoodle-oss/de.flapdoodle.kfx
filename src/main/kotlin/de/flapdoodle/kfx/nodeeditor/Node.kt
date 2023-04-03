package de.flapdoodle.kfx.nodeeditor

import de.flapdoodle.kfx.layout.absolute.AbsolutePane
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.scene.paint.Color

class Node(val name: String) : Pane(), IsNode {
  init {
    children.add(BorderPane().apply {
      center = Label(name)
      bottom = Button("...")
    })
    border= Border(BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii(3.0), BorderWidths.DEFAULT))

  }

  override fun onFocus() {
    border= Border(BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, CornerRadii(3.0), BorderWidths.DEFAULT))
  }

  override fun onBlur() {
    border= Border(BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii(3.0), BorderWidths.DEFAULT))
  }

  fun resizeTo(width: Double, height: Double) {
    val pW = computePrefWidth(width)
    val pH = computePrefHeight(height)
    this.prefWidth = width.coerceAtLeast(pW)
    this.prefHeight = height.coerceAtLeast(pH)
  }
}