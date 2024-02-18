package de.flapdoodle.kfx.controls.graphmodeleditor.types

import de.flapdoodle.kfx.controls.grapheditor.types.SlotId

data class VertexSlotId<T>(val vertexId: VertexId<T>, val slotId: SlotId)
