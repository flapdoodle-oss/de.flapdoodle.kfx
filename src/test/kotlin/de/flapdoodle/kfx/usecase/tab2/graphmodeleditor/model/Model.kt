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
package de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.model

import de.flapdoodle.kfx.collections.Change
import de.flapdoodle.kfx.collections.Diff
import de.flapdoodle.kfx.controls.grapheditor.types.SlotId
import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.types.VertexId

data class Model<V>(
  val vertexList: List<Vertex<V>> = emptyList(),
  val edgeSet: Set<Edge<V>> = emptySet()
) {
  private val vertexMap = vertexList.associateBy { it.id }

  private val vertexSlotSet: Set<Pair<VertexId<V>, SlotId>> = vertexList.flatMap {
    vertex -> vertex.slots.map { property -> vertex.id to property.id }
  }.toSet()
  private val usedPropertySets: Set<Pair<VertexId<V>, SlotId>> = edgeSet.flatMap { edge ->
    listOf(edge.startVertex to edge.startSlot, edge.endVertex to edge.endSlot)
  }.toSet()

  fun vertex(id: VertexId<V>) = requireNotNull(vertexMap[id]) { "could not get vertex $id"}
  fun add(vararg vertex: Vertex<V>): Model<V> {
    return copy(vertexList = vertexList + vertex)
  }

  fun replace(old: Vertex<V>, new: Vertex<V>): Model<V> {
    require(old.id == new.id) {"id does not match: ${old.id} != ${new.id}"}
    val changes = Vertex.slotChanges(old, new)
    val removedButUsedProperties = usedPropertySets.intersect(changes.removed.map { old.id to it.id }.toSet())
    require(removedButUsedProperties.isEmpty()) {"can not remove used properties: $removedButUsedProperties"}
    return copy(vertexList = vertexList - old + new)
  }

  fun remove(id: VertexId<V>): Model<V> {
    val vertex = vertex(id)
    val changes = Vertex.slotChanges(vertex, vertex.copy(slots = emptyList()))
    val removedButUsedProperties = usedPropertySets.intersect(changes.removed.map { vertex.id to it.id }.toSet())
    require(removedButUsedProperties.isEmpty()) {"can not remove used properties: $removedButUsedProperties"}
    return copy(vertexList = vertexList - vertex)
  }

  fun add(edge: Edge<V>): Model<V> {
    require(!edgeSet.contains(edge)) { "edge already there" }
    require(vertexSlotSet.contains(edge.startVertex to edge.startSlot)) { "could not find start vertex ${edge.startVertex}, property ${edge.startSlot}"}
    require(vertexSlotSet.contains(edge.endVertex to edge.endSlot)) { "could not find end vertex ${edge.endVertex}, property ${edge.endSlot}"}
    return copy(edgeSet = edgeSet + edge)
  }

  fun remove(edge: Edge<V>): Model<V> {
    require(edgeSet.contains(edge)) { "edge not found" }
    return copy(edgeSet = edgeSet - edge)
  }

  companion object {
    fun <T> vertexChanges(old: Model<T>, new: Model<T>): Change<Vertex<T>> {
      return Diff.between(old.vertexList, new.vertexList, Vertex<T>::id)
//      val removed = (old.vertexMap.keys - new.vertexMap.keys).map { old.vertex(it) }.toSet()
//      val sameIds = old.vertexMap.keys.intersect(new.vertexMap.keys).toSet()
//      val notChanged = sameIds.filter { old.vertex(it) == new.vertex(it) }.map { new.vertex(it) }.toSet()
//      val modified = sameIds.filter { old.vertex(it) != new.vertex(it) }.map { old.vertex(it) to new.vertex(it) }.toSet()
//      val added = (new.vertexMap.keys - old.vertexMap.keys).map { new.vertex(it) }.toSet()
//
//      return Change(
//        removed = removed,
//        notChanged = notChanged,
//        modified = modified,
//        added = added
//      )
    }

    fun <T> edgeChanges(old: Model<T>, new: Model<T>): Change<Edge<T>> {
      return Diff.between(old.edgeSet, new.edgeSet, { it} )
//      val removed = (old.edgeSet - new.edgeSet)
//      val notChanged = old.edgeSet.intersect(new.edgeSet)
//      val added = (new.edgeSet - old.edgeSet)
//
//      return Change(
//        removed = removed,
//        notChanged = notChanged,
//        modified = emptySet(),
//        added = added
//      )
    }
  }
}
