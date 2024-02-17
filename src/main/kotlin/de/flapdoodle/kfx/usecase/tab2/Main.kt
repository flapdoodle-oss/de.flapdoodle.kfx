package de.flapdoodle.kfx.usecase.tab2

import de.flapdoodle.kfx.controls.grapheditor.GraphEditorModelAdapter
import de.flapdoodle.kfx.controls.grapheditor.model.*
import de.flapdoodle.kfx.extensions.withAnchors
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.kfx.usecase.tab2.graph.DummyVertexContentFactory
import de.flapdoodle.kfx.usecase.tab2.graph.Models
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import java.util.concurrent.atomic.AtomicInteger

class Main() : BorderPane() {
  private val model = SimpleObjectProperty(Models.testModel())
  private val selectedVertex = SimpleObjectProperty<VertexId<String>>()
  private val selectedEdge = SimpleObjectProperty<Edge<String>>()
  private val vertexCounter = AtomicInteger(0)
  private val slotCounter = AtomicInteger(0)

  private val eventListener = ModelEventListener<String> { event ->
    when (event) {
      is ModelEvent.ConnectTo -> {
        model.value = model.value.add(Edge(event.startVertex, event.startSlot, event.endVertex, event.endSlot))
      }
      else -> {

      }
    }
    true
  }

  init {
//    background = Background.fill(Color.DARKGRAY)
//    children.add(Button("Hi"))
    center = WeightGridPane()
      .withAnchors(all = 0.0)
      .also { gridPane ->
        gridPane.setColumnWeight(0, 1000.0)
        gridPane.setColumnWeight(1, 1.0)
        gridPane.setRowWeight(1, 1.0)

        gridPane.children.add(GraphEditorModelAdapter(model, eventListener, DummyVertexContentFactory).also { editor ->
          editor.selectedVerticesProperty().subscribe { selection ->
            if (selection.size == 1) {
              selectedVertex.value = selection.first()
            } else {
              selectedVertex.value = null
            }
          }
          editor.selectedEdgesProperty().subscribe { selection ->
            if (selection.size == 1) {
              selectedEdge.value = selection.first()
            } else {
              selectedEdge.value = null
            }
          }
          WeightGridPane.setPosition(editor, 0, 0)
        })
//        gridPane.children.add(Button("Hi").also { hi ->
//          hi.minWidth = 40.0
//          hi.maxWidth = 1600.0
//          hi.maxHeight = 800.0
//          WeightGridPane.setPosition(hi, 0, 0)
//        })
        gridPane.children.add(Button("!!").also { button ->
          button.minWidth = 40.0
          button.maxWidth = 80.0
          button.maxHeight = 40.0
          WeightGridPane.setPosition(button, 1, 0)
        })
      }
    bottom = FlowPane().also { flowPane ->
      flowPane.children.addAll(
        Button("+").also { button ->
          button.onAction = EventHandler {
            model.set(model.get().add(Vertex("Name#"+vertexCounter.incrementAndGet(), "X")))
          }
        },
        Button("-->").also { button ->
          button.visibleProperty().bind(selectedVertex.map { it != null })
          button.onAction = EventHandler {
            val vertexId = selectedVertex.value
            val vertex = model.get().vertex(vertexId)
            model.set(model.get().replace(vertex, vertex.add(Slot("x#"+slotCounter.incrementAndGet(), Slot.Mode.IN, Position.LEFT))))
          }
        },
        Button("<--").also { button ->
          button.visibleProperty().bind(selectedVertex.map { it != null })
          button.onAction = EventHandler {
            val vertexId = selectedVertex.value
            val vertex = model.get().vertex(vertexId)
            model.set(model.get().replace(vertex, vertex.add(Slot("y#"+slotCounter.incrementAndGet(), Slot.Mode.OUT, Position.RIGHT))))
          }
        },
        Button("X").also { button ->
          button.visibleProperty().bind(selectedEdge.map { it != null })
          button.onAction = EventHandler {
            val egde = selectedEdge.value
            model.set(model.get().remove(egde))
          }
        }
      )
    }
  }
}