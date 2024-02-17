package de.flapdoodle.kfx.controls.grapheditor.model

import de.flapdoodle.kfx.controls.grapheditor.types.SlotId

sealed class Action<V> {
  data class AddVertex<V>(val vertex: Vertex<V>): Action<V>()
  data class AddSlot<V>(val vertex: VertexId<V>, val slot: Slot): Action<V>()
  data class AddEdge<V>(val edge: Edge<V>): Action<V>()

  data class ChangeVertex<V>(val vertex: VertexId<V>, val change: Vertex<V>): Action<V>()
  data class ChangeSlot<V>(val vertex: VertexId<V>, val slot: SlotId, val change: Slot): Action<V>()
  data class ChangeEdge<V>(val edge: EdgeId<V>, val change: Edge<V>): Action<V>()

  data class RemoveVertex<V>(val vertex: VertexId<V>): Action<V>()
  data class RemoveSlot<V>(val vertex: VertexId<V>, val slot: SlotId): Action<V>()
  data class RemoveEdge<V>(val edge: EdgeId<V>): Action<V>()

  companion object {
    fun <V> syncActions(old: Model<V>, current: Model<V>): List<Action<V>> {
      var actions = emptyList<Action<V>>()
//      println("old: $old")
//      println("current: $current")
      if (old != current) {
        val vertexChanges = Model.vertexChanges(old, current)
        val edgeChanges = Model.edgeChanges(old, current)

        actions = actions + edgeChanges.removed.map { edge ->
          RemoveEdge(edge.id)
        }

        actions = actions + vertexChanges.removed.flatMap { vertex ->
          vertex.slots.map { RemoveSlot(vertex.id, it.id) } + listOf(RemoveVertex(vertex.id))
        }

        actions = actions + vertexChanges.added.flatMap { vertex ->
          listOf(AddVertex(vertex)) + vertex.slots.map { AddSlot(vertex.id, it) }
        }

        actions = actions + vertexChanges.modified.flatMap { (oldVertex, currentVertex) ->
          var vertexActions = emptyList<Action<V>>()
          if (oldVertex != currentVertex) {
            val slotChanges = Vertex.slotChanges(oldVertex, currentVertex)

            vertexActions = vertexActions + slotChanges.removed.map { RemoveSlot(currentVertex.id, it.id) }
            vertexActions = vertexActions + ChangeVertex(currentVertex.id, currentVertex)
            vertexActions = vertexActions + slotChanges.modified.map { (oldSlot, newSlot) ->
              ChangeSlot(currentVertex.id,oldSlot.id, newSlot)
            }
            vertexActions = vertexActions + slotChanges.added.map { AddSlot(currentVertex.id, it) }

          }
          vertexActions
        }

        actions = actions + edgeChanges.modified.map { (oldEdge, currentEdge) ->
          ChangeEdge(oldEdge.id, currentEdge)
        }

        actions = actions + edgeChanges.added.map { edge ->
          AddEdge(edge)
        }
      }
      return actions
    }
  }
}