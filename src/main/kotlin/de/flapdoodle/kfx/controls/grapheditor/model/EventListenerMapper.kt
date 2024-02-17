package de.flapdoodle.kfx.controls.grapheditor.model

import de.flapdoodle.kfx.controls.grapheditor.GraphEditor
import de.flapdoodle.kfx.controls.grapheditor.events.Event
import de.flapdoodle.kfx.controls.grapheditor.events.EventListener
import de.flapdoodle.kfx.controls.grapheditor.types.VertexId
import de.flapdoodle.kfx.types.Id

class EventListenerMapper<T>(
  private val delegate: ModelEventListener<T>,
  private val vertexIdMapper: (VertexId) -> de.flapdoodle.kfx.controls.grapheditor.model.VertexId<T>
) : EventListener {
  override fun onEvent(graphEditor: GraphEditor, event: Event): Boolean {
    return when (event) {
      is Event.TryToConnect -> {
        delegate.onEvent(ModelEvent.TryToConnect(vertexIdMapper(event.start.vertexId), event.start.slotId))
      }

      is Event.TryToConnectTo -> {
        delegate.onEvent(
          ModelEvent.TryToConnectTo(vertexIdMapper(event.start.vertexId), event.start.slotId, vertexIdMapper(event.end.vertexId), event.end.slotId)
        )
      }

      is Event.ConnectTo -> {
        delegate.onEvent(ModelEvent.ConnectTo(vertexIdMapper(event.start.vertexId), event.start.slotId, vertexIdMapper(event.end.vertexId), event.end.slotId))
      }
    }
  }
}