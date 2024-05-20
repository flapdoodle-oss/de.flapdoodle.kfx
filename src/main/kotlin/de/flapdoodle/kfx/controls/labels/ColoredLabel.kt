package de.flapdoodle.kfx.controls.labels

import de.flapdoodle.kfx.layout.StackLikeRegion
import javafx.scene.text.TextFlow

class ColoredLabel : StackLikeRegion() {
  private val textFlow = TextFlow()

  init {
    children.add(textFlow)
  }
}