package de.flapdoodle.kfx.controls.grapheditor.events

import de.flapdoodle.kfx.controls.grapheditor.types.VertexSlotId
import javafx.geometry.Point2D

sealed class Event {
  data class TryToConnect(val start: VertexSlotId): Event()
  data class TryToConnectTo(val start: VertexSlotId, val end: VertexSlotId): Event()
  data class ConnectTo(val start: VertexSlotId, val end: VertexSlotId): Event()
  data class Click(val position: Point2D): Event()
}