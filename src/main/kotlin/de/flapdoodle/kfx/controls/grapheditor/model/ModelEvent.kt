package de.flapdoodle.kfx.controls.grapheditor.model

import de.flapdoodle.kfx.controls.grapheditor.types.SlotId
import de.flapdoodle.kfx.types.Id

sealed class ModelEvent<T> {
  data class TryToConnect<T>(val vertex: VertexId<T>, val slot:  SlotId): ModelEvent<T>()
  data class TryToConnectTo<T>(val startVertex: VertexId<T>, val startSlot: SlotId, val endVertex: VertexId<T>, val endSlot: SlotId): ModelEvent<T>()
  data class ConnectTo<T>(val startVertex: VertexId<T>, val startSlot: SlotId, val endVertex: VertexId<T>, val endSlot: SlotId): ModelEvent<T>()
}