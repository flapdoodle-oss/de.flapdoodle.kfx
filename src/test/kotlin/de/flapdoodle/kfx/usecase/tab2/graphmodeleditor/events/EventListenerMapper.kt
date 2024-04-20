/*
 * Copyright (C) 2022
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.events

import de.flapdoodle.kfx.controls.grapheditor.GraphEditor
import de.flapdoodle.kfx.controls.grapheditor.events.Event
import de.flapdoodle.kfx.controls.grapheditor.events.EventListener
import de.flapdoodle.kfx.controls.grapheditor.types.VertexId

class EventListenerMapper<T>(
  private val delegate: ModelEventListener<T>,
  private val vertexIdMapper: (VertexId) -> de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.types.VertexId<T>
) : EventListener {
  override fun onEvent(graphEditor: GraphEditor, event: Event): Boolean {
    return when (event) {
      is Event.TryToConnect -> delegate.onEvent(ModelEvent.TryToConnect(vertexIdMapper(event.start.vertexId), event.start.slotId))
      is Event.TryToConnectTo -> delegate.onEvent(
        ModelEvent.TryToConnectTo(vertexIdMapper(event.start.vertexId), event.start.slotId, vertexIdMapper(event.end.vertexId), event.end.slotId)
      )
      is Event.ConnectTo -> delegate.onEvent(ModelEvent.ConnectTo(vertexIdMapper(event.start.vertexId), event.start.slotId, vertexIdMapper(event.end.vertexId), event.end.slotId))
      is Event.VertexMoved -> delegate.onEvent(ModelEvent.VertexMoved(vertexIdMapper(event.vertexId), event.layoutPosition))
      else -> {
        throw IllegalArgumentException("this can not happen: $event")
      }
    }
  }
}