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
package de.flapdoodle.kfx.usecase.tab2

import de.flapdoodle.kfx.colors.HashedColors
import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.GraphEditorModelAdapter
import de.flapdoodle.kfx.controls.grapheditor.slots.Position
import de.flapdoodle.kfx.controls.grapheditor.slots.Slot
import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.commands.Command
import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.events.ModelEvent
import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.events.ModelEventListener
import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.types.VertexId
import de.flapdoodle.kfx.extensions.withAnchors
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.kfx.usecase.tab2.defaults.DummyVertexContentFactory
import de.flapdoodle.kfx.usecase.tab2.defaults.Models
import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.model.Edge
import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.model.Vertex
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
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
      is ModelEvent.VertexMoved -> {
        println("vertex ${event.vertex} moved to ${event.layoutPosition}")
      }
      else -> {

      }
    }
    true
  }
  private val editorAdapter = GraphEditorModelAdapter(model, eventListener, DummyVertexContentFactory).also { editor ->
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
  }

  init {
//    background = Background.fill(Color.DARKGRAY)
//    children.add(Button("Hi"))
    addEventFilter(KeyEvent.KEY_RELEASED) { event ->
      if (event.code == KeyCode.ESCAPE) {
        editorAdapter.execute(Command.Abort())
      }
    }

    center = WeightGridPane()
      .withAnchors(all = 0.0)
      .also { gridPane ->
        gridPane.setColumnWeight(0, 1000.0)
        gridPane.setColumnWeight(1, 1.0)
        gridPane.setRowWeight(1, 1.0)

        gridPane.children.add(editorAdapter)
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
            editorAdapter.execute(Command.AskForPosition(onSuccess = { pos ->
              model.set(model.get().add(Vertex("Name#"+vertexCounter.incrementAndGet(), "X", position = pos)))
            }))
          }
        },
        Button("-").also { button ->
          button.visibleProperty().bind(selectedVertex.map { it != null })
          button.managedProperty().bind(button.visibleProperty())
          button.onAction = EventHandler {
            val vertexId = selectedVertex.value
            model.set(model.get().remove(vertexId))
          }
        },
        Button("?").also { button ->
          button.visibleProperty().bind(selectedVertex.map { it != null })
          button.managedProperty().bind(button.visibleProperty())
          button.onAction = EventHandler {
            val vertexId = selectedVertex.value
            editorAdapter.execute(Command.FindVertex(vertexId) { println("found:)") })
          }
        },
        Button("-->").also { button ->
          button.visibleProperty().bind(selectedVertex.map { it != null })
          button.managedProperty().bind(button.visibleProperty())
          button.onAction = EventHandler {
            val vertexId = selectedVertex.value
            val vertex = model.get().vertex(vertexId)
            model.set(model.get().replace(vertex, vertex.add(Slot("x#"+slotCounter.incrementAndGet(), Slot.Mode.IN, Position.LEFT, HashedColors.hashedColor(slotCounter.get())))))
          }
        },
        Button("<--").also { button ->
          button.visibleProperty().bind(selectedVertex.map { it != null })
          button.managedProperty().bind(button.visibleProperty())
          button.onAction = EventHandler {
            val vertexId = selectedVertex.value
            val vertex = model.get().vertex(vertexId)
            model.set(model.get().replace(vertex, vertex.add(Slot("y#"+slotCounter.incrementAndGet(), Slot.Mode.OUT, Position.RIGHT, HashedColors.hashedColor(slotCounter.get())))))
          }
        },
        Button("X").also { button ->
          button.visibleProperty().bind(selectedEdge.map { it != null })
          button.managedProperty().bind(button.visibleProperty())
          button.onAction = EventHandler {
            val egde = selectedEdge.value
            model.set(model.get().remove(egde))
          }
        }
      )
    }
  }
}