package de.flapdoodle.kfx.controls.grapheditor.model

import de.flapdoodle.kfx.controls.grapheditor.types.SlotId

data class Edge<T>(
  val startVertex: VertexId<T>,
  val startSlot: SlotId,
  val endVertex: VertexId<T>,
  val endSlot: SlotId,
  val id: EdgeId<T> = EdgeId(),
) {
  init {
    val startPair = startVertex to startSlot
    val endPair = endVertex to endSlot
    require(startPair != endPair) { "start($startPair) == end($endPair)" }
  }
}
