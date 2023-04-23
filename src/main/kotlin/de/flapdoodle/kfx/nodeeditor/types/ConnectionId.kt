package de.flapdoodle.kfx.nodeeditor.types

data class ConnectionId(
  val source: NodeSlotId,
  val destination: NodeSlotId
) 