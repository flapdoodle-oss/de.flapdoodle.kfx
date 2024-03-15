package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.extensions.withAnchors
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane

open class HeaderColumn<T: Any>(
  internal open val column: Column<T, out Any>
) : StackPane() {

  private val background = WeightGridPane()
//  private val background = Pane()
  private val contentPane = StackPane()

  private val content = SimpleObjectProperty<Node>()

  init {
    isFocusTraversable = true

    cssClassName("header-column")
    background.cssClassName("background")

    children.add(background)
    WeightGridPane.setPosition(contentPane, 0, 0, HPos.CENTER, VPos.CENTER)
    background.children.add(contentPane)
    
    content.addListener { observable, oldValue, newValue ->
      if (oldValue!=null) contentPane.children.remove(oldValue)
      if (newValue!=null) contentPane.children.add(newValue)
    }
  }

  fun setContent(node: Node?) {
//    if (node!=null) WeightGridPane.setPosition(node, 0, 0, HPos.CENTER, VPos.CENTER)
    content.value = node
  }
}
