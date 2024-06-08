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

import de.flapdoodle.kfx.bindings.NodeContainerProperty
import de.flapdoodle.kfx.controls.grapheditor.slots.Position
import de.flapdoodle.kfx.controls.grapheditor.slots.Slot
import de.flapdoodle.kfx.controls.grapheditor.slots.SlotsPane
import de.flapdoodle.kfx.controls.grapheditor.types.IsSelectable
import de.flapdoodle.kfx.controls.grapheditor.types.SlotId
import de.flapdoodle.kfx.controls.grapheditor.types.VertexId
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.extensions.layoutPosition
import de.flapdoodle.kfx.layout.StackLikeRegion
import de.flapdoodle.kfx.types.LayoutBounds
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority

class Vertex(
  name: String,
  controls: (VertexId) -> List<Node> = { emptyList() }
) : BorderPane(), IsSelectable {
  val vertexId=VertexId()
  val registry = SimpleObjectProperty<Registry>()

  companion object {
    fun onlyNodes(node: Node): List<Vertex> {
      return if (node is Parent) {
        node.childrenUnmodifiable.filterIsInstance<Vertex>()
      } else {
        emptyList<Vertex>()
      }
    }
  }

  private val contentWrapper=StackLikeRegion.PaneLike().apply {
    cssClassName("content")
  }
  private val _content=NodeContainerProperty.of<Node>("content", contentWrapper::getChildren)
  private val connectors = FXCollections.observableArrayList<Slot>()
  private val selected = SimpleBooleanProperty(false)
  private val nameProperty = SimpleStringProperty(name)

  var content: Node
    get() = _content.get()
    set(value) { _content.set(value)}

  fun nameProperty() = nameProperty

  override fun toString(): String {
    return "Vertex(${nameProperty().value})"
  }

  init {
    bindCss("vertex")
//    cssClassName("vertex")
//    stylesheets += javaClass.getResource("Vertex.css").toExternalForm()

//    setMargin(contentWrapper, Insets(10.0))

    center = contentWrapper.apply {
      Markers.markAsContent(this)
    }
    top = NodeHeader(nameProperty, controls(vertexId)).apply {
      Markers.markAsDragBar(this)
    }
    left = SlotsPane(registry, vertexId, connectors, Position.LEFT)
    right = SlotsPane(registry, vertexId, connectors, Position.RIGHT)
    bottom = SlotsPane(registry, vertexId, connectors, Position.BOTTOM)

    selected.subscribe { it ->
      Styles.Selected.set(this, it)
    }
  }

  fun resizeTo(bounds: LayoutBounds) {
    val width = bounds.size.width
    val height = bounds.size.height

    val pW = computeMinWidth(width)
    val pH = computeMinHeight(height)

    prefWidth = width.coerceAtLeast(pW)
    prefHeight = height.coerceAtLeast(pH)
    layoutPosition = bounds.layoutPosition
  }

  fun addConnector(connector: Slot) {
    connectors.add(connector)
  }

  fun removeConnector(slotId: SlotId) {
    connectors.removeIf { it.id==slotId }
  }

  fun replaceConnector(slotId: SlotId, connector: Slot) {
    require(slotId == connector.id) {"slotId != connector.id ($slotId != ${connector.id})"}
    connectors.replaceAll { if (it.id==slotId) connector else it }
  }

  class NodeHeader(label: ReadOnlyProperty<String>, controls: List<Node>) : HBox() {
    init {
      cssClassName("vertex-header")
      isMouseTransparent = false
//      alignment = Pos.BASELINE_CENTER
//      background = Background(BackgroundFill(Color.GREY, CornerRadii(2.0), Insets(1.0)))
      children.add(Label().apply {
        cssClassName("title")
        textProperty().bind(label)
        setHgrow(this, Priority.ALWAYS)
        maxWidth = Double.MAX_VALUE
      })
      controls.forEach { button ->
        children.add(button)
        Markers.markAsExcluded(button)
      }
    }
  }

  fun selectedProperty(): ReadOnlyProperty<Boolean> = selected

  override fun isSelected(): Boolean {
    return selected.get()
  }

  override fun select(value: Boolean) {
    selected.value = value
    requestFocus()
  }

  fun focused(): Boolean {
    return Styles.Focused.enabled(this)
  }

  fun focus() {
    Styles.Focused.enable(this)
  }

  fun blur() {
    Styles.Focused.disable(this)
  }


}