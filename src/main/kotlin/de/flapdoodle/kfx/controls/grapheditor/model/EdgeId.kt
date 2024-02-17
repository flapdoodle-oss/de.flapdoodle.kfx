package de.flapdoodle.kfx.controls.grapheditor.model

import de.flapdoodle.kfx.types.Key

data class EdgeId<T>(val id: Int = nextId()) {
  companion object {
    private fun nextId(): Int {
      return Key.nextId(EdgeId::class)
    }
  }
}