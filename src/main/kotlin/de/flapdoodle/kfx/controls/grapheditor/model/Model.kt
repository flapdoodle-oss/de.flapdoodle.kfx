package de.flapdoodle.kfx.controls.grapheditor.model

import de.flapdoodle.kfx.controls.grapheditor.types.SlotId
import de.flapdoodle.kfx.types.Id

data class Model<V>(
  val vertexList: List<Vertex<V>> = emptyList(),
  val edgeList: List<Edge<V>> = emptyList()
) {
  private val vertexMap = vertexList.associateBy { it.id }
  private val edgeMap = edgeList.associateBy { it.id }

  private val vertexSlotSet: Set<Pair<VertexId<V>, SlotId>> = vertexList.flatMap {
    vertex -> vertex.slots.map { property -> vertex.id to property.id }
  }.toSet()
  private val usedPropertySets: Set<Pair<VertexId<V>, SlotId>> = edgeList.flatMap { edge ->
    listOf(edge.startVertex to edge.startSlot, edge.endVertex to edge.endSlot)
  }.toSet()

  fun vertex(id: VertexId<V>) = requireNotNull(vertexMap[id]) { "could not get vertex $id"}
  fun edge(id: EdgeId<V>) = requireNotNull(edgeMap[id]) { "could not get edge $id"}

  fun add(vertex: Vertex<V>): Model<V> {
    return copy(vertexList = vertexList + vertex)
  }

  fun replace(old: Vertex<V>, new: Vertex<V>): Model<V> {
    require(old.id == new.id) {"id does not match: ${old.id} != ${new.id}"}
    val changes = Vertex.slotChanges(old, new)
    val removedButUsedProperties = usedPropertySets.intersect(changes.removed.map { old.id to it.id })
    require(removedButUsedProperties.isEmpty()) {"can not remove used properties: $removedButUsedProperties"}
    return copy(vertexList = vertexList - old + new)
  }

  fun add(edge: Edge<V>): Model<V> {
    require(vertexSlotSet.contains(edge.startVertex to edge.startSlot)) { "could not find start vertex ${edge.startVertex}, property ${edge.startSlot}"}
    require(vertexSlotSet.contains(edge.endVertex to edge.endSlot)) { "could not find end vertex ${edge.endVertex}, property ${edge.endSlot}"}
    return copy(edgeList = edgeList + edge)
  }

  companion object {
    fun <T> vertexChanges(old: Model<T>, new: Model<T>): Change<Vertex<T>> {
      val removed = (old.vertexMap.keys - new.vertexMap.keys).map { old.vertex(it) }.toSet()
      val sameIds = old.vertexMap.keys.intersect(new.vertexMap.keys).toSet()
      val notChanged = sameIds.filter { old.vertex(it) == new.vertex(it) }.map { new.vertex(it) }.toSet()
      val modified = sameIds.filter { old.vertex(it) != new.vertex(it) }.map { old.vertex(it) to new.vertex(it) }.toSet()
      val added = (new.vertexMap.keys - old.vertexMap.keys).map { new.vertex(it) }.toSet()

      return Change(
        removed = removed,
        notChanged = notChanged,
        modified = modified,
        added = added
      )
    }

    fun <T> edgeChanges(old: Model<T>, new: Model<T>): Change<Edge<T>> {
      val removed = (old.edgeMap.keys - new.edgeMap.keys).map { old.edge(it) }.toSet()
      val sameIds = old.edgeMap.keys.intersect(new.edgeMap.keys).toSet()
      val notChanged = sameIds.filter { old.edge(it) == new.edge(it) }.map { new.edge(it) }.toSet()
      val modified = sameIds.filter { old.edge(it) != new.edge(it) }.map { old.edge(it) to new.edge(it) }.toSet()
      val added = (new.edgeMap.keys - old.edgeMap.keys).map { new.edge(it) }.toSet()

      return Change(
        removed = removed,
        notChanged = notChanged,
        modified = modified,
        added = added
      )
    }
  }
}
