package de.flapdoodle.kfx.controls.grapheditor

import de.flapdoodle.kfx.bindings.Subscriptions
import de.flapdoodle.kfx.controls.grapheditor.model.*
import de.flapdoodle.kfx.controls.grapheditor.types.EdgeId
import de.flapdoodle.kfx.controls.grapheditor.types.VertexId
import de.flapdoodle.kfx.controls.grapheditor.types.VertexSlotId
import de.flapdoodle.kfx.extensions.plus
import de.flapdoodle.kfx.extensions.withAnchors
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.util.Subscription

class GraphEditorModelAdapter<V>(
  model: ObjectProperty<Model<V>>,
  modelEventListener: ModelEventListener<V>,
  private val vertexFactory: VertexContentFactory<V>
) : AnchorPane() {
  private val graphEditor = GraphEditor(eventListener = EventListenerMapper(modelEventListener, ::vertexId)).withAnchors(all = 10.0)
  private var subscriptions = Subscription.EMPTY
  
  private var vertexIdMapping = emptyMap<de.flapdoodle.kfx.controls.grapheditor.model.VertexId<V>, Vertex>()
  private var reverseVertexIdMapping = emptyMap<VertexId, de.flapdoodle.kfx.controls.grapheditor.model.VertexId<V>>()
  private var vertexIdContentMapping = emptyMap<de.flapdoodle.kfx.controls.grapheditor.model.VertexId<V>, VertexContent<V>>()

  private var edgeIdMapping = emptyMap<de.flapdoodle.kfx.controls.grapheditor.model.Edge<V>, Edge>()
  private var reverseEdgeIdMapping = emptyMap<EdgeId, de.flapdoodle.kfx.controls.grapheditor.model.Edge<V>>()

  private val selectedVertices = SimpleObjectProperty<Set<de.flapdoodle.kfx.controls.grapheditor.model.VertexId<V>>>(emptySet())
  private val selectedEdges = SimpleObjectProperty<Set<de.flapdoodle.kfx.controls.grapheditor.model.Edge<V>>>(emptySet())

  init {
    children.add(graphEditor)
    
    apply(Action.syncActions(Model(), model.value))
    subscriptions += model.subscribe { old, current ->
      apply(Action.syncActions(old, current))
    }
  }

  fun selectedVerticesProperty(): ReadOnlyProperty<Set<de.flapdoodle.kfx.controls.grapheditor.model.VertexId<V>>> = selectedVertices
  fun selectedEdgesProperty(): ReadOnlyProperty<Set<de.flapdoodle.kfx.controls.grapheditor.model.Edge<V>>> = selectedEdges

  private fun vertexId(id: VertexId): de.flapdoodle.kfx.controls.grapheditor.model.VertexId<V> {
    return requireNotNull(reverseVertexIdMapping[id]) { "could not get vertex id for $id" }
  }

  private fun vertexId(id: de.flapdoodle.kfx.controls.grapheditor.model.VertexId<V>): VertexId {
    return requireNotNull(vertexIdMapping[id]) { "could not get vertex id for $id" }.vertexId
  }

  private fun apply(action: List<Action<V>>) {
    action.forEach { action ->
      println("-------------------------------")
      println("action -> $action")
      when (action) {
        is Action.AddVertex -> {
          graphEditor.addVertex(Vertex(action.vertex.name).also { vertex ->
            vertexIdMapping = vertexIdMapping + (action.vertex.id to vertex)
            reverseVertexIdMapping = reverseVertexIdMapping + (vertex.vertexId to action.vertex.id)
            val vertexContent = vertexFactory.vertexContent(action.vertex.data)
            vertexIdContentMapping = vertexIdContentMapping + (action.vertex.id to vertexContent)
            vertex.content = vertexContent.node
            vertex.selectedProperty().subscribe { it -> changeSelection(action.vertex.id, it) }
          })
        }
        is Action.AddSlot -> {
          val vertex = requireNotNull(vertexIdMapping.get(action.vertex)) {"could not get vertex for ${action.vertex}"}
          vertex.addConnector(action.slot)
        }
        is Action.AddEdge -> {
          val start = VertexSlotId(vertexId(action.edge.startVertex), action.edge.startSlot)
          val end = VertexSlotId(vertexId(action.edge.endVertex), action.edge.endSlot)
          graphEditor.addEdge(Edge(start, end).also { edge ->
            edgeIdMapping = edgeIdMapping + (action.edge to edge)
            reverseEdgeIdMapping = reverseEdgeIdMapping + (edge.edgeId to action.edge)
            Subscriptions.add(edge, edge.selectedProperty().subscribe { it -> changeSelection(action.edge, it) })
          })
        }
        is Action.RemoveEdge -> {
          val edge = requireNotNull(edgeIdMapping.get(action.edge)) {"edge not found: ${action.edge}"}
          graphEditor.removeEdge(edge)
          edgeIdMapping = edgeIdMapping - action.edge
          reverseEdgeIdMapping = reverseEdgeIdMapping - edge.edgeId
          changeSelection(action.edge, false)
          Subscriptions.unsubscribeAll(edge)
        }
        is Action.ChangeVertex -> {
          val vertex = requireNotNull(vertexIdMapping.get(action.vertex)) {"could not get vertex for ${action.vertex}"}
          vertex.nameProperty().value = action.change.name

          val vertexContent = requireNotNull(vertexIdContentMapping.get(action.vertex)) {"could not get vertexContent for ${action.vertex}"}
          vertexContent.valueModel.value = action.change.data
        }
        else -> println("not implemented: $action")
      }
    }
  }

  private fun changeSelection(vertex: de.flapdoodle.kfx.controls.grapheditor.model.VertexId<V>, selection: Boolean) {
    val current = selectedVertices.get()
    selectedVertices.value = if (selection) {
      current + vertex
    } else {
      current - vertex
    }
  }

  private fun changeSelection(egde: de.flapdoodle.kfx.controls.grapheditor.model.Edge<V>, selection: Boolean) {
    val current = selectedEdges.get()
    selectedEdges.value = if (selection) {
      current + egde
    } else {
      current - egde
    }
  }
}