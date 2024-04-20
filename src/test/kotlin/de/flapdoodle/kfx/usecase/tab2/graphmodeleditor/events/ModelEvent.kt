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
package de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.events

import de.flapdoodle.kfx.controls.grapheditor.types.SlotId
import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.types.VertexId
import javafx.geometry.Point2D

sealed class ModelEvent<T> {
  data class TryToConnect<T>(val vertex: VertexId<T>, val slot:  SlotId): ModelEvent<T>()
  data class TryToConnectTo<T>(val startVertex: VertexId<T>, val startSlot: SlotId, val endVertex: VertexId<T>, val endSlot: SlotId): ModelEvent<T>()
  data class ConnectTo<T>(val startVertex: VertexId<T>, val startSlot: SlotId, val endVertex: VertexId<T>, val endSlot: SlotId): ModelEvent<T>()
  data class VertexMoved<T>(val vertex: VertexId<T>, val layoutPosition: Point2D): ModelEvent<T>()
}