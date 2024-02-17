package de.flapdoodle.kfx.controls.grapheditor.types

import de.flapdoodle.kfx.types.Id

data class SlotId(val id: Id<SlotId> = Id.nextId(SlotId::class))