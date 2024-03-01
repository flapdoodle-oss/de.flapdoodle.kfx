package de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.types

import de.flapdoodle.kfx.types.Key

data class VertexId<T>(val id: Int = nextId()) {
  companion object {
    private fun nextId(): Int {
      return Key.nextId(VertexId::class)
    }
  }
}