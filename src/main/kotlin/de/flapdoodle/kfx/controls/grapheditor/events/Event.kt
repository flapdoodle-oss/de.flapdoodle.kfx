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
package de.flapdoodle.kfx.controls.grapheditor.events

import de.flapdoodle.kfx.controls.grapheditor.types.VertexId
import de.flapdoodle.kfx.controls.grapheditor.types.VertexSlotId
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D

sealed class Event {
  data class TryToConnect(val start: VertexSlotId): Event()
  data class TryToConnectTo(val start: VertexSlotId, val end: VertexSlotId): Event()
  data class ConnectTo(val start: VertexSlotId, val end: VertexSlotId): Event()
  data class VertexMoved(val vertexId: VertexId, val layoutPosition: Point2D): Event()
  data class VertexResized(val vertexId: VertexId, val layoutPosition: Point2D, val size: Dimension2D): Event()
}