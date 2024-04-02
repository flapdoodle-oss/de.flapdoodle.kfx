package de.flapdoodle.kfx.controls.bettertable

import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.text.TextAlignment
import javafx.util.StringConverter

object Cells {

  fun asPosition(textAlignment: TextAlignment): Pos {
    return when (textAlignment) {
      TextAlignment.RIGHT -> Pos.CENTER_RIGHT
      TextAlignment.LEFT -> Pos.CENTER_LEFT
      TextAlignment.CENTER -> Pos.CENTER
      TextAlignment.JUSTIFY -> Pos.CENTER
    }
  }
}