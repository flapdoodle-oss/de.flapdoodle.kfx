package de.flapdoodle.kfx.extensions

import javafx.scene.paint.Color

object Colors {
  fun asCss(color: Color): String {
    return String.format(
      "#%02X%02X%02X",
      (color.red * 255).toInt(),
      (color.green * 255).toInt(),
      (color.blue * 255).toInt()
    )
  }
}