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
package de.flapdoodle.kfx.usecase.tab2.defaults

import de.flapdoodle.kfx.colors.HashedColors
import de.flapdoodle.kfx.controls.grapheditor.slots.Position
import de.flapdoodle.kfx.controls.grapheditor.slots.Slot
import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.model.Edge
import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.model.Model
import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.model.Vertex
import javafx.geometry.Point2D

object Models {
  fun emptyModel() = Model<String>()

  fun testModel(): Model<String> {
    val a = Vertex("a", "A", listOf(Slot("X-->", Slot.Mode.OUT, Position.RIGHT, HashedColors.hashedColor("X-->"))), position = Point2D(10.0, 30.0))
    val b = Vertex("b", "B", listOf(Slot("-->X", Slot.Mode.IN, Position.LEFT, HashedColors.hashedColor("-->X"))), position = Point2D(200.0, 90.0))
    return emptyModel()
      .add(a)
      .add(b)
      .add(Edge(a.id, a.slots[0].id, b.id, b.slots[0].id))
  }
}