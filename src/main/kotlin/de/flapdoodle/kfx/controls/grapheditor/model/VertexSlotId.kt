package de.flapdoodle.kfx.controls.grapheditor.model

import de.flapdoodle.kfx.controls.grapheditor.types.SlotId

data class VertexSlotId<T>(val vertexId: VertexId<T>, val slotId: SlotId)
