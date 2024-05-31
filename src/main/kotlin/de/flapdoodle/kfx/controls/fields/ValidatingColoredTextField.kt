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
package de.flapdoodle.kfx.controls.fields

import de.flapdoodle.kfx.bindings.ObjectBindings
import de.flapdoodle.kfx.controls.Tooltips
import de.flapdoodle.kfx.controls.labels.ColoredLabel
import de.flapdoodle.kfx.converters.ValidatingConverter
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.bindings.node.ChildNodeFilter
import de.flapdoodle.kfx.bindings.node.ChildNodeProperty
import de.flapdoodle.kfx.bindings.node.ChildNodeProperty.Companion.andThen
import de.flapdoodle.kfx.bindings.node.NodeProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.TextField
import javafx.scene.control.Tooltip
import javafx.scene.layout.Border
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text

class ValidatingColoredTextField<T: Any>(
  val converter: ValidatingConverter<T>,
  val default: T? = null,
  mapException: (Exception) -> String = { it.localizedMessage },
  val mapColors: (T?, String?) -> List<ColoredLabel.Part> = { _, _ -> emptyList() },
  onError: (TextField, String?) -> Unit = { textfield, error ->
    if (error != null) {
      textfield.tooltip = Tooltips.tooltip(error)
      textfield.border = Border.stroke(Color.RED)
    } else {
      textfield.tooltip = null
      textfield.border = null
    }
  }
): StackPane(), ValidatingField<T> {

  private val delegate = ValidatingTextField(converter, default, mapException, onError)

  private val paneProperty = ChildNodeProperty(delegate, ChildNodeFilter.isInstance(Pane::class))
  private val panePosition = paneProperty.property(Pane::boundsInParentProperty)

  private val textClipNodeProperty = paneProperty.property(Pane::clipProperty)
  private val textClipProperty = NodeProperty(textClipNodeProperty, Node::boundsInParentProperty)

  private val textProperty = paneProperty.andThen(ChildNodeFilter.isInstance(Text::class))
  private val textBounds = textProperty.property(Text::boundsInParentProperty)

  private val coloredLabelClip = Rectangle()
  private val colors = SimpleObjectProperty<List<ColoredLabel.Part>>(emptyList())
  private val coloredLabel = ColoredLabel(delegate.textProperty(), colors).apply {
    cssClassName("colored-label")
    isManaged = false
  }
  private val coloredLabelPane = Pane().apply {
    children += coloredLabel
    clip = coloredLabelClip
    isManaged = false
  }

  var tooltip: Tooltip?
    set(value) { delegate.tooltip = value }
    get() { return delegate.tooltip }

  init {
    bindCss("colored-text-field")

    colors.bind(ObjectBindings.merge(valueProperty(), delegate.textProperty()) { v, t ->
      mapColors(v, t ?: "")
    })

    setAlignment(delegate, Pos.TOP_LEFT)
    setAlignment(coloredLabel, Pos.TOP_LEFT)

    children.addAll(delegate, coloredLabelPane)

    coloredLabel.isFocusTraversable = false
    coloredLabel.isMouseTransparent = true
//    coloredLabel.opacity = 1.0

    panePosition.addListener { _,_,pos ->
      if (pos!=null) {
        coloredLabelPane.resizeRelocate(pos.minX, pos.minY, pos.width, pos.height)
      }
    }
    textBounds.addListener { observable, oldValue, newValue ->
      if (newValue!=null) {
//        val n = sceneToLocal(newValue)
        val n = newValue
        coloredLabel.resizeRelocate(n.minX, n.minY, n.width, n.height)
      }
    }

    textClipProperty.addListener { observable, oldValue, newValue ->
      if (newValue!=null) {
//        println("clip: $newValue")
        // what?
        coloredLabelClip.isSmooth = true
        coloredLabelClip.x = 0.0
        coloredLabelClip.width = newValue.width
        coloredLabelClip.height = newValue.height
      }
    }
  }

  override fun get() = delegate.get()
  override fun set(value: T?) = delegate.set(value)
  override fun hasError() = delegate.hasError()
  override fun errorMessage() = delegate.errorMessage()
  override fun lastErrorProperty() = delegate.lastErrorProperty()
  override fun valueProperty() = delegate.valueProperty()

  var onAction: EventHandler<ActionEvent>?
    get() = delegate.onAction
    set(value) { delegate.onAction = value }
}
