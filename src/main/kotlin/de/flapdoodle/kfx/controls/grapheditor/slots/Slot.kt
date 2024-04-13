package de.flapdoodle.kfx.controls.grapheditor.slots

import de.flapdoodle.kfx.controls.grapheditor.types.SlotId
import javafx.scene.paint.Color

data class Slot(
  val name: String,
  val mode: Mode,
  val position: Position,
  val color: Color,
  val id: SlotId = SlotId()
) {

  enum class Mode { IN, OUT }
}


