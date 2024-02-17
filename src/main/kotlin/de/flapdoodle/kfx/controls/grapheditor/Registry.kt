package de.flapdoodle.kfx.controls.grapheditor

import de.flapdoodle.kfx.bindings.NestedValueBinding
import de.flapdoodle.kfx.bindings.ObjectBindings
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
    edge.init(this::scenePosition)
  }

  fun unregisterConnection(edge: Edge) {
    edge.dispose()
  }

  private fun nodeByIdProperty(id: VertexId): ObservableValue<Vertex?> {
    return nodesProperty.map { it[id] }
  }

  private fun scenePosition(vertexSlotId: VertexSlotId): ObjectBindings.DefaultIfNull<ColoredAngleAtPoint2D> {
    return NestedValueBinding.of(nodeSlotsProperty.map { it[vertexSlotId] }) { it }
      .defaultIfNull(Values.constantObject(ColoredAngleAtPoint2D(0.0, 0.0, 0.0, Color.BLACK)))
  }

  fun registerSlot(vertexSlotId: VertexSlotId, positionInScene: ObservableValue<ColoredAngleAtPoint2D>) {
    nodeSlots[vertexSlotId] = positionInScene
  }

  fun unregisterSlot(vertexSlotId: VertexSlotId) {
    nodeSlots.remove(vertexSlotId)
  }

  fun scenePositionOf(source: VertexSlotId): ColoredAngleAtPoint2D? {
    return scenePosition(source).value
  }
}