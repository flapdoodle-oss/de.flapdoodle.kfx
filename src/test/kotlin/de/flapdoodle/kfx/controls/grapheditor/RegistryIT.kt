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

import de.flapdoodle.kfx.colors.HashedColors
import de.flapdoodle.kfx.controls.grapheditor.events.Event
import de.flapdoodle.kfx.controls.grapheditor.slots.Position
import de.flapdoodle.kfx.controls.grapheditor.slots.Slot
import de.flapdoodle.kfx.controls.grapheditor.types.VertexSlotId
import de.flapdoodle.kfx.extensions.layoutPosition
import javafx.geometry.Point2D
import javafx.scene.Scene
import javafx.stage.Stage
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import java.util.function.Predicate

@ExtendWith(ApplicationExtension::class)
class RegistryIT {

  val slotInA = Slot("->a", Slot.Mode.IN, Position.LEFT, HashedColors.hashedColor("a"))
  val slotInB = Slot("->b", Slot.Mode.IN, Position.LEFT, HashedColors.hashedColor("b"))
  val slotInC = Slot("->c", Slot.Mode.IN, Position.LEFT, HashedColors.hashedColor("c"))

  val slotOutX = Slot("x->", Slot.Mode.OUT, Position.RIGHT, HashedColors.hashedColor("x"))
  val slotOutY = Slot("y->", Slot.Mode.OUT, Position.RIGHT, HashedColors.hashedColor("y"))
  val slotOutZ = Slot("z->", Slot.Mode.OUT, Position.RIGHT, HashedColors.hashedColor("z"))

  val slotAgg1 = Slot("->1", Slot.Mode.IN, Position.BOTTOM, HashedColors.hashedColor("1"))
  val slotAgg2 = Slot("2->", Slot.Mode.OUT, Position.BOTTOM, HashedColors.hashedColor("2"))
  val slotAgg3 = Slot("->3", Slot.Mode.IN, Position.BOTTOM, HashedColors.hashedColor("3"))

  @Start
  private fun createElement(stage: Stage) {
    val vertexA = Vertex("A").apply {
      id = "testee:A"
      layoutPosition = Point2D(10.0, 10.0)
//      addConnector(slotInA)
//      addConnector(slotOutX)
    }
    val vertexB = Vertex("B").apply {
      id = "testee:B"
      layoutPosition = Point2D(200.0, 10.0)
//      addConnector(slotInB)
//      addConnector(slotAgg1)
    }
    val vertexC = Vertex("C").apply {
      id = "testee:C"
      layoutPosition = Point2D(100.0, 100.0)
//      addConnector(slotInA)
//      addConnector(slotOutY)
    }

    val graphEditor = GraphEditor { editor, event ->
      when (event) {
        is Event.TryToConnect -> true
        is Event.TryToConnectTo -> true
        is Event.ConnectTo -> {
          editor.addEdge(Edge(event.start, event.end))
          true
        }

        else -> true
      }
    } //.withAnchors(all = 10.0)
    graphEditor.id = "testee"
    graphEditor.addVertex(vertexA, vertexB, vertexC)
//    graphEditor.addEdge(
//      Edge(VertexSlotId(vertexA.vertexId, slotOutX.id), VertexSlotId(vertexB.vertexId, slotInB.id))
//    )

    stage.scene = Scene(graphEditor, 600.0, 400.0)
    stage.show()
  }

  @Test
  fun positionMustRecover(robot: FxRobot) {
    val graphEditor = robot.lookup(Predicate { it.id == "testee" }).queryAs(GraphEditor::class.java)
    val vertexA = robot.lookup(Predicate { it.id == "testee:A" }).queryAs(Vertex::class.java)
    val vertexB = robot.lookup(Predicate { it.id == "testee:B" }).queryAs(Vertex::class.java)
    val vertexC = robot.lookup(Predicate { it.id == "testee:C" }).queryAs(Vertex::class.java)

    val edgeAxBa = Edge(VertexSlotId(vertexA.vertexId, slotOutX.id), VertexSlotId(vertexB.vertexId, slotInA.id))
    val edgeAyBb = Edge(VertexSlotId(vertexA.vertexId, slotOutY.id), VertexSlotId(vertexB.vertexId, slotInB.id))
    val edgeCzBc = Edge(VertexSlotId(vertexC.vertexId, slotOutZ.id), VertexSlotId(vertexB.vertexId, slotInC.id))

    robot.interact {
      vertexB.addConnector(slotInA)
      vertexB.addConnector(slotInB)
      vertexB.addConnector(slotInC)

      vertexA.addConnector(slotOutX)
      vertexA.addConnector(slotOutY)

      vertexC.addConnector(slotOutZ)

      graphEditor.addEdge(edgeAxBa)
      graphEditor.addEdge(edgeAyBb)
      graphEditor.addEdge(edgeCzBc)
    }

//    Thread.sleep(500)
    
    robot.interact {
      vertexB.removeConnector(slotInA.id)
      vertexB.removeConnector(slotInB.id)
      vertexB.addConnector(slotInB)
      vertexB.addConnector(slotInA)
    }

//    Thread.sleep(10000)
  }
}