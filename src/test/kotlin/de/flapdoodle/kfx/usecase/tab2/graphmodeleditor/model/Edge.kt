package de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.model

import de.flapdoodle.kfx.controls.grapheditor.types.SlotId
import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.types.VertexId

data class Edge<T>(
  val startVertex: VertexId<T>,
  val startSlot: SlotId,
  val endVertex: VertexId<T>,
  val endSlot: SlotId
) {
  init {
    val startPair = startVertex to startSlot
    val endPair = endVertex to endSlot
    require(startPair != endPair) { "start($startPair) == end($endPair)" }
  }
}
