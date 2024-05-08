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

import de.flapdoodle.kfx.bindings.and
import de.flapdoodle.kfx.controls.Tooltips
import de.flapdoodle.kfx.controls.grapheditor.Markers
import de.flapdoodle.kfx.controls.grapheditor.Registry
import de.flapdoodle.kfx.controls.grapheditor.types.VertexId
import de.flapdoodle.kfx.controls.grapheditor.types.VertexSlotId
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.extensions.onAttach
import de.flapdoodle.kfx.extensions.onBindToScene
import de.flapdoodle.kfx.extensions.plus
import de.flapdoodle.kfx.types.ColoredAngleAtPoint2D
import javafx.beans.value.ObservableValue
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.control.Tooltip
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.ArcTo
import javafx.scene.shape.Circle
import javafx.scene.shape.LineTo
import javafx.scene.shape.MoveTo
import javafx.scene.shape.Path
import javafx.util.Duration
import javafx.util.Subscription

class SlotPane(
  registry: ObservableValue<Registry>,
  private val vertexId: VertexId,
  private val slot: Slot,
  position: Position
) : StackPane() {
  private val color = slot.color
  private val radius = 5.0
  private val path = Path().apply {
    fill = color
    strokeWidth = 0.0
    isSnapToPixel = true

    elements.addAll(when (position) {
      Position.LEFT -> listOf(
        MoveTo(0.0, -radius),
        LineTo(radius, -radius),
        ArcTo(radius, radius, 0.0, radius, radius, false, true),
        LineTo(radius, radius),
        LineTo(0.0, radius),
      )
      Position.RIGHT -> listOf(
        MoveTo(0.0, -radius),
        LineTo(-radius, -radius),
        ArcTo(radius, radius, 0.0, -radius, radius, false, false),
        LineTo(-radius, radius),
        LineTo(0.0, radius),
      )
      Position.BOTTOM -> listOf(
        MoveTo(-radius, 0.0),
        LineTo(-radius, -radius),
        ArcTo(radius, radius, 0.0, radius, -radius, false, true),
        LineTo(radius, -radius),
        LineTo(radius, 0.0),
      )
    })
//      ArcTo(5.0,5.0,90.0,0.0,0.0,true,false)
  }
  private val circle = Circle(5.0, color).apply {
//    Tooltip.install(this, Tooltip(slot.name))
  }
//  private val label = Label(slot.name)
  private var subscriptions = Subscription.EMPTY
  private val space = Region()
  private val angle = when (position) {
    Position.LEFT -> 180.0
    Position.BOTTOM -> 90.0
    Position.RIGHT -> 0.0
  }

//  private val pointInSceneProperty = circle.localToSceneTransformProperty().and(circle.radiusProperty()).map { transform, number ->
//    ColoredAngleAtPoint2D(transform.transform(Point2D(0.0, 0.0)), angle, color)
//  }

  private val pointInSceneProperty = path.localToSceneTransformProperty().map { transform ->
    ColoredAngleAtPoint2D(transform.transform(Point2D(0.0, 0.0)), angle, color)
  }


  init {
    cssClassName("slot")

    Markers.markAsNodeSlot(circle, VertexSlotId(vertexId, slot.id))
    Markers.markAsNodeSlot(path, VertexSlotId(vertexId, slot.id))

    val wrapper = when (position) {
      Position.LEFT -> HBox().apply { alignment = Pos.CENTER }
      Position.RIGHT -> HBox().apply { alignment = Pos.CENTER }
      Position.BOTTOM -> VBox().apply { alignment = Pos.CENTER }
    }

    Tooltip.install(wrapper, Tooltips.tooltip(slot.name))

    HBox.setHgrow(space, Priority.ALWAYS)
    VBox.setVgrow(space, Priority.ALWAYS)

    when (position) {
      Position.LEFT -> wrapper.children.addAll(path, space)
      Position.RIGHT -> wrapper.children.addAll(space, path)
      Position.BOTTOM -> wrapper.children.addAll(space, path)
    }

    children.addAll(wrapper)

    onBindToScene {
      registry.value?.registerSlot(VertexSlotId(vertexId, slot.id), pointInSceneProperty)
      Subscription {
        registry.value?.unregisterSlot(VertexSlotId(vertexId, slot.id))
      }
    }
//    onAttach {
//      registry.value?.registerSlot(VertexSlotId(vertexId, slot.id), pointInSceneProperty)
//    }.onDetach {
//      registry.value?.unregisterSlot(VertexSlotId(vertexId, slot.id))
//    }

    subscriptions += registry.subscribe { oldValue, newValue ->
      oldValue?.unregisterSlot(VertexSlotId(vertexId, slot.id))
      newValue?.registerSlot(VertexSlotId(vertexId, slot.id), pointInSceneProperty)
    }
  }

}

