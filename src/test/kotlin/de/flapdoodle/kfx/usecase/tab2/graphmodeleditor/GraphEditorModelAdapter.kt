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
package de.flapdoodle.kfx.usecase.tab2.graphmodeleditor

import de.flapdoodle.kfx.bindings.Subscriptions
import de.flapdoodle.kfx.collections.Mapping
import de.flapdoodle.kfx.controls.grapheditor.Edge
import de.flapdoodle.kfx.controls.grapheditor.GraphEditor
import de.flapdoodle.kfx.controls.grapheditor.Vertex
import de.flapdoodle.kfx.controls.grapheditor.types.EdgeId
import de.flapdoodle.kfx.controls.grapheditor.types.VertexId
import de.flapdoodle.kfx.controls.grapheditor.types.VertexSlotId
import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.commands.Command
import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.events.EventListenerMapper
import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.events.ModelEventListener
import de.flapdoodle.kfx.extensions.layoutPosition
import de.flapdoodle.kfx.extensions.plus
import de.flapdoodle.kfx.extensions.withAnchors
import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.model.*
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.layout.AnchorPane
import javafx.util.Subscription

class GraphEditorModelAdapter<V>(
  model: ReadOnlyObjectProperty<Model<V>>,
  modelEventListener: ModelEventListener<V>,
  private val vertexFactory: VertexContentFactory<V>
) : AnchorPane() {
  private val graphEditor = GraphEditor(eventListener = EventListenerMapper(modelEventListener, ::vertexId)).withAnchors(all = 10.0)
  private var subscriptions = Subscription.EMPTY

  private class VertexAndContent<V>(val vertex: Vertex, val content: VertexContent<V>)
  private val vertexMapping = Mapping<de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.types.VertexId<V>, VertexId, VertexAndContent<V>>()
  private val edgeMapping = Mapping<de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.model.Edge<V>, EdgeId, Edge>()

  private val selectedVertices = SimpleObjectProperty<Set<de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.types.VertexId<V>>>(emptySet())
  private val selectedEdges = SimpleObjectProperty<Set<de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.model.Edge<V>>>(emptySet())

  init {
    children.add(graphEditor)
    
    apply(Action.syncActions(Model(), model.value))
    subscriptions += model.subscribe { old, current ->
      apply(Action.syncActions(old, current))
    }
  }

  fun selectedVerticesProperty(): ReadOnlyProperty<Set<de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.types.VertexId<V>>> = selectedVertices
  fun selectedEdgesProperty(): ReadOnlyProperty<Set<de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.model.Edge<V>>> = selectedEdges

  fun execute(command: Command<V>) {
    val mapped = when (command) {
      is Command.Abort -> de.flapdoodle.kfx.controls.grapheditor.commands.Command.Abort()
      is Command.AskForPosition -> de.flapdoodle.kfx.controls.grapheditor.commands.Command.AskForPosition(command.onSuccess)
      is Command.FindVertex -> {
        val pos = vertexMapping[command.vertex]!!.vertex.layoutPosition
        de.flapdoodle.kfx.controls.grapheditor.commands.Command.PanTo(pos,command.onSuccess)
      }
    }
    graphEditor.execute(mapped)
  }

  private fun vertexId(id: VertexId): de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.types.VertexId<V> {
    return requireNotNull(vertexMapping.key(id)) { "could not get vertex id for $id" }
  }

  private fun vertexId(id: de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.types.VertexId<V>): VertexId {
    return requireNotNull(vertexMapping.reverseKey(id)) { "could not get vertex id for $id" }
  }

  private fun apply(action: List<Action<V>>) {
    action.forEach { action ->
//      println("-------------------------------")
//      println("action -> $action")
      when (action) {
        is Action.AddVertex -> {
          graphEditor.addVertex(Vertex(action.vertex.name).also { vertex ->
            val vertexContent = vertexFactory.vertexContent(action.vertex.data)
            vertex.layoutPosition = action.vertex.position
            vertex.content = vertexContent.node
            vertexMapping.add(action.vertex.id, vertex.vertexId, VertexAndContent(vertex, vertexContent))
            Subscriptions.add(vertex, vertex.selectedProperty().subscribe { it -> changeSelection(action.vertex.id, it) })
          })
        }
        is Action.ChangeVertex -> {
          vertexMapping.with(action.vertex) {
            it.vertex.nameProperty().value = action.change.name
            it.content.valueModel.value = action.change.data
          }
        }
        is Action.RemoveVertex -> {
          vertexMapping.remove(action.vertex) {
            graphEditor.removeVertex(it.vertex)
            changeSelection(action.vertex, false)
            Subscriptions.unsubscribeAll(it.vertex)
          }
        }
        is Action.AddSlot -> {
          vertexMapping.with(action.vertex) {
            it.vertex.addConnector(action.slot)
          }
        }
        is Action.RemoveSlot -> {
          vertexMapping.with(action.vertex) {
            it.vertex.removeConnector(action.slot)
          }
        }
        is Action.AddEdge -> {
          val start = VertexSlotId(vertexId(action.edge.startVertex), action.edge.startSlot)
          val end = VertexSlotId(vertexId(action.edge.endVertex), action.edge.endSlot)
          graphEditor.addEdge(Edge(start, end).also { edge ->
            edgeMapping.add(action.edge, edge.edgeId, edge)
            Subscriptions.add(edge, edge.selectedProperty().subscribe { it -> changeSelection(action.edge, it) })
          })
        }
        is Action.RemoveEdge -> {
          edgeMapping.remove(action.edge) {
            graphEditor.removeEdge(it)
            changeSelection(action.edge, false)
            Subscriptions.unsubscribeAll(it)
          }
        }
        else -> println("not implemented: $action")
      }
    }
  }

  private fun changeSelection(vertex: de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.types.VertexId<V>, selection: Boolean) {
    val current = selectedVertices.get()
    selectedVertices.value = if (selection) {
      current + vertex
    } else {
      current - vertex
    }
  }

  private fun changeSelection(egde: de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.model.Edge<V>, selection: Boolean) {
    val current = selectedEdges.get()
    selectedEdges.value = if (selection) {
      current + egde
    } else {
      current - egde
    }
  }
}