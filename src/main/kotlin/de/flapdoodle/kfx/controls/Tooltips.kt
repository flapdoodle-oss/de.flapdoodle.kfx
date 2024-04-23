package de.flapdoodle.kfx.controls

import javafx.scene.control.Tooltip
import javafx.util.Duration

object Tooltips {

  fun tooltip(message: String): Tooltip {
    return Tooltip(message).apply {
      showDelay = Duration.millis(50.0)
      hideDelay = Duration.millis(5.0)
    }
  }
}