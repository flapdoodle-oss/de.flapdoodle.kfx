package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.layout.StackPane

open abstract class AbstractHeaderColumn<T: Any>(
  internal open val column: Column<T, out Any>,
  val cssClassName: String,
) : StackPane() {

  private val background = WeightGridPane()
  private val contentPane = StackPane()

  private val content = SimpleObjectProperty<Node>()

  init {
    isFocusTraversable = true

    cssClassName(cssClassName)
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
    content.value = node
  }
}
