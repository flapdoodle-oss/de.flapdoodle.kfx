package de.flapdoodle.kfx.controls.grapheditor.slots

import de.flapdoodle.kfx.controls.grapheditor.types.SlotId
import javafx.scene.paint.Color

data class Slot(
  val name: String,
  val mode: Mode,
  val position: Position,
  val color: Color? = hashedColor(name, mode),
  val id: SlotId = SlotId()
) {

  enum class Mode { IN, OUT }

  companion object {
    private val colorsNames = arrayOf(
      "#ffffff", "#fb6b1d", "#e83b3b", "#831c5d", "#c32454", "#f04f78", "#f68181", "#fca790", "#e3c896",
      "#ab947a", "#966c6c", "#625565", "#3e3546", "#0b5e65", "#0b8a8f", "#1ebc73", "#91db69", "#fbff86",
      "#fbb954", "#cd683d", "#9e4539", "#7a3045", "#6b3e75", "#905ea9", "#a884f3", "#eaaded", "#8fd3ff",
      "#4d9be6", "#4d65b4", "#484a77", "#30e1b9", "#8ff8e2"
    )
    private val colors = colorsNames.map(Color::web)

    fun hashedColor(name: String, mode: Mode): Color? {
      val hash = name.hashCode() + mode.hashCode()
      val index = (if (hash > 0) hash else -hash) % colors.size
      return colors[index]
    }
  }
}


