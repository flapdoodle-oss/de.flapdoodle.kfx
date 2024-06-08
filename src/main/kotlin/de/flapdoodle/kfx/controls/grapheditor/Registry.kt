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
package de.flapdoodle.kfx.controls.grapheditor

import de.flapdoodle.kfx.bindings.MapProperty
import de.flapdoodle.kfx.bindings.ObservableMaps
import de.flapdoodle.kfx.bindings.Values
import de.flapdoodle.kfx.bindings.defaultIfNull
import de.flapdoodle.kfx.controls.grapheditor.types.VertexId
import de.flapdoodle.kfx.controls.grapheditor.types.VertexSlotId
import de.flapdoodle.kfx.types.ColoredAngleAtPoint2D
import javafx.beans.property.ReadOnlyMapWrapper
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableMap
import javafx.scene.paint.Color

class Registry {
  private val nodes: ObservableMap<VertexId, Vertex> = FXCollections.observableHashMap()
  private val nodesProperty = ReadOnlyMapWrapper(nodes)
  private val nodeSlots: ObservableMap<VertexSlotId, ObservableValue<ColoredAngleAtPoint2D>> = FXCollections.observableHashMap()
  private val nodeSlotsProperty = ReadOnlyMapWrapper(nodeSlots)

  fun registerNode(vertex: Vertex) {
    nodes[vertex.vertexId] = vertex
  }

  fun unregisterNode(vertex: Vertex) {
    nodes.remove(vertex.vertexId)
  }

  fun registerConnection(edge: Edge) {
    edge.init(this::scenePositionProperty)
  }

  fun unregisterConnection(edge: Edge) {
    edge.dispose()
  }

  private fun nodeByIdProperty(id: VertexId): ObservableValue<Vertex?> {
    return nodesProperty.map { it[id] }
  }

  private fun scenePositionProperty(vertexSlotId: VertexSlotId): ObservableValue<ColoredAngleAtPoint2D> {
    return ObservableMaps.valueOf(nodeSlots, vertexSlotId).defaultIfNull(Values.constantObject(ColoredAngleAtPoint2D(0.0, 0.0, 0.0, Color.BLACK)))
//     return NestedProperty(nodeSlotsProperty.map { it[vertexSlotId] }, { it })
//       .defaultIfNull(Values.constantObject(ColoredAngleAtPoint2D(0.0, 0.0, 0.0, Color.BLACK)))
  }

  fun registerSlot(vertexSlotId: VertexSlotId, positionInScene: ObservableValue<ColoredAngleAtPoint2D>) {
    nodeSlots[vertexSlotId] = positionInScene
  }

  fun unregisterSlot(vertexSlotId: VertexSlotId) {
    nodeSlots.remove(vertexSlotId)
  }

  fun scenePositionOf(source: VertexSlotId): ColoredAngleAtPoint2D? {
    return nodeSlots[source]?.value
  }
}