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
package de.flapdoodle.kfx.controls.grapheditor.slots

import de.flapdoodle.kfx.bindings.ObservableLists
import de.flapdoodle.kfx.controls.grapheditor.Registry
import de.flapdoodle.kfx.controls.grapheditor.types.VertexId
import de.flapdoodle.kfx.extensions.unsubscribeOnDetach
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox

class SlotsPane(
  private val registry: ObservableValue<Registry>,
  private val vertexId: VertexId,
  slots: ObservableList<Slot>,
  private val position: Position
) : Pane() {

  init {
    val filtered = slots.filtered { it.position == position }

    val wrapper = when (position) {
      Position.LEFT -> VBox().apply { spacing = 2.0 }
      Position.RIGHT -> VBox().apply { spacing = 2.0 }
      Position.BOTTOM -> HBox().apply { spacing = 2.0 }
    }

    unsubscribeOnDetach {
      ObservableLists.syncWith(filtered, wrapper.children) { c ->
        SlotPane(registry, vertexId, c, position)
      }
    }

    children.add(wrapper)
  }


}