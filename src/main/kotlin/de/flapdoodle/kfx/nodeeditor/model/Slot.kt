package de.flapdoodle.kfx.nodeeditor.model

import java.util.*

data class Slot(val name: String, val mode: Mode) {
  val uuid= UUID.randomUUID()

  enum class Mode { IN, OUT}
}
