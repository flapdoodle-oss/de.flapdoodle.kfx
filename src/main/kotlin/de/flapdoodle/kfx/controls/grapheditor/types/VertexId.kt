package de.flapdoodle.kfx.controls.grapheditor.types

import de.flapdoodle.kfx.types.Id

data class VertexId(val id: Id<VertexId> = Id.nextId(VertexId::class))