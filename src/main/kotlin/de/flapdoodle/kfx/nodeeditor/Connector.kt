package de.flapdoodle.kfx.nodeeditor

import java.util.*

data class Connector(val name: String, val mode: Mode) {
  val uuid= UUID.randomUUID()

  enum class Mode { IN, OUT}
}
