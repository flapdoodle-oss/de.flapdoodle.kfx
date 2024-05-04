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

import de.flapdoodle.kfx.controls.bettertable.TableFactory
import de.flapdoodle.kfx.colors.HashedColors
import de.flapdoodle.kfx.controls.grapheditor.events.Event
import de.flapdoodle.kfx.controls.grapheditor.events.EventListener
import de.flapdoodle.kfx.controls.grapheditor.slots.Position
import de.flapdoodle.kfx.controls.grapheditor.slots.Slot
import de.flapdoodle.kfx.controls.grapheditor.types.VertexSlotId
import de.flapdoodle.kfx.extensions.layoutPosition
import javafx.application.Application
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.stage.Stage

class GraphEditorSampler {

  class Sample : Application() {
    override fun start(stage: Stage) {
      val wrapper = BorderPane()
      val slotInA = Slot("a", Slot.Mode.IN, Position.LEFT, HashedColors.hashedColor("a"))
      val slotInB = Slot("b", Slot.Mode.IN, Position.LEFT, HashedColors.hashedColor("b"))
      val slotOutX = Slot("x", Slot.Mode.OUT, Position.RIGHT, HashedColors.hashedColor("x"))
      val slotOutY = Slot("y", Slot.Mode.OUT, Position.RIGHT, HashedColors.hashedColor("y"))
      val slotOutZ = Slot("z", Slot.Mode.OUT, Position.RIGHT, HashedColors.hashedColor("z"))
      val slotAgg1 = Slot("1", Slot.Mode.IN, Position.BOTTOM, HashedColors.hashedColor("1"))
      val slotAgg2 = Slot("2", Slot.Mode.OUT, Position.BOTTOM, HashedColors.hashedColor("2"))
      val slotAgg3 = Slot("3", Slot.Mode.IN, Position.BOTTOM, HashedColors.hashedColor("3"))

      val vertexOne = Vertex("one").apply {
        layoutPosition = Point2D(100.0, 50.0)
        content = TableFactory.table().apply {
          minWidth = 50.0
          minHeight = 50.0
        }
        addConnector(slotInA)
        addConnector(slotOutX)
        addConnector(slotOutY)
        addConnector(slotOutZ)
      }
      val vertexTwo = Vertex("two").apply {
        val node = this
        content = Button("Helloooo:)").apply {
          onMouseClicked = EventHandler {
            println("clicked...")
            it.consume()
            node.toFront()
          }
        }
        addConnector(slotInA)
        addConnector(slotInB)
        addConnector(slotAgg1)
        addConnector(slotAgg2)
        addConnector(slotAgg3)
      }
      val vertex3 = Vertex("3").apply {
        val node = this
        layoutPosition = Point2D(200.0, 0.0)
        content = Button("Noop").apply {
          onMouseClicked = EventHandler {
            println("clicked...")
            it.consume()
            node.toFront()
          }
        }
        addConnector(slotInA)
        addConnector(slotOutX)
        addConnector(slotAgg1)
      }

      val listener = EventListener { editor, event ->
        when (event) {
          is Event.TryToConnect -> {
            if (event.start.slotId == slotOutX.id) {
              println("deny connection from ${event.start}")
              false
            }
            else true
          }
          is Event.TryToConnectTo -> {
            if (event.end.slotId == slotOutX.id) {
              println("deny connection to ${event.end}")
              false
            }
            else true
          }
          is Event.ConnectTo -> {
            editor.addEdge(Edge(event.start, event.end))
            true
          }
          else -> {
            true
          }
        }
      }
      val graphEditor = GraphEditor(listener) //.withAnchors(all = 10.0)
      graphEditor.addVertex(vertexOne, vertexTwo, vertex3)
      graphEditor.addEdge(
        Edge(VertexSlotId(vertexOne.vertexId, slotOutX.id), VertexSlotId(vertexTwo.vertexId, slotInA.id))
      )

      var lastAddedVertex: Vertex? = null

      wrapper.center = graphEditor
      wrapper.bottom = FlowPane().also { flowPane ->
        flowPane.children.add(Button("+Vertex").also { button ->
          button.onAction = EventHandler {
            val vertex = Vertex("X")
            graphEditor.addVertex(vertex)
            lastAddedVertex = vertex
          }
        })
        flowPane.children.add(Button("+Vertex+In").also { button ->
          button.onAction = EventHandler {
            val vertex = Vertex("X")
            graphEditor.addVertex(vertex)
            vertex.addConnector(Slot("u", Slot.Mode.IN, Position.LEFT, HashedColors.hashedColor("u")))
            lastAddedVertex = vertex
          }
        })
        flowPane.children.add(Button("+In").also { button ->
          button.onAction = EventHandler {
            if (lastAddedVertex!=null) {
              lastAddedVertex!!.addConnector(Slot("u", Slot.Mode.IN, Position.LEFT, HashedColors.hashedColor("u")))
            }
          }
        })
      }
      stage.scene = Scene(wrapper, 600.0, 400.0)
      stage.show()
    }
  }

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      Application.launch(Sample::class.java, *args)
    }
  }
}