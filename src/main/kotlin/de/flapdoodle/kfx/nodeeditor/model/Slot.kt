package de.flapdoodle.kfx.nodeeditor.model

import de.flapdoodle.kfx.nodeeditor.types.SlotId
import javafx.scene.paint.Color
import java.util.*

data class Slot(
  val name: String,
  val mode: Mode,
  val position: Position,
  val color: Color? = null
) {
  val id = SlotId()

  enum class Mode { IN, OUT }
}
