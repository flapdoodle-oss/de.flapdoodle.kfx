package de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.types

import de.flapdoodle.kfx.controls.grapheditor.types.SlotId

data class VertexSlotId<T>(val vertexId: VertexId<T>, val slotId: SlotId)
