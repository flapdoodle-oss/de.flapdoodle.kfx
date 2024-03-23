package de.flapdoodle.kfx.controls.bettertable

import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.text.TextAlignment
import javafx.util.StringConverter

object Cells {
  fun <T : Any> createTextField(
    value: T?,
    converter: StringConverter<T>,
    commitEdit: (T?) -> Unit,
    cancelEdit: () -> Unit
  ): TextField {
    val textField = TextField()
    textField.text = converter.toString(value)

    textField.onKeyReleased = EventHandler { t: KeyEvent ->
      if (t.code == KeyCode.ENTER) {
        t.consume()
        commitEdit(converter.fromString(textField.text))
      }
      if (t.code == KeyCode.ESCAPE) {
        t.consume()
        cancelEdit()
      }
    }
    return textField
  }

  fun asPosition(textAlignment: TextAlignment): Pos {
    return when (textAlignment) {
      TextAlignment.RIGHT -> Pos.CENTER_RIGHT
      TextAlignment.LEFT -> Pos.CENTER_LEFT
      TextAlignment.CENTER -> Pos.CENTER
      TextAlignment.JUSTIFY -> Pos.CENTER
    }
  }
}