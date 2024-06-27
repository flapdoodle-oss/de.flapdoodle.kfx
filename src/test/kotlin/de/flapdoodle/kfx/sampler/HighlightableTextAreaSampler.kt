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
package de.flapdoodle.kfx.sampler

import javafx.application.Application
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Bounds
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.effect.BlendMode
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Path
import javafx.scene.shape.Rectangle
import javafx.stage.Stage

class HighlightableTextAreaSampler{

  class Sample : Application() {

    override fun start(stage: Stage) {
      val root = VBox()
      root.spacing = 10.0
      root.padding = Insets(10.0)
      val sc = Scene(root, 600.0, 600.0)
      stage.scene = sc
      stage.show()

      val highlightableTextArea = HighlightableTextAreaSampler.HighlightableTextArea()
      highlightableTextArea.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")
      highlightableTextArea.textArea.isWrapText = true
      highlightableTextArea.textArea.style = "-fx-font-size: 20px;"
      VBox.setVgrow(highlightableTextArea, Priority.ALWAYS)

      val highlight = Button("Highlight")
      val stF = TextField("40")
      val enF = TextField("50")
      val hb = HBox(highlight, stF, enF)
      hb.spacing = 10.0
      highlight.onAction = EventHandler { e: ActionEvent? -> highlightableTextArea.highlight(stF.text.toInt(), enF.text.toInt()) }

      val remove = Button("Remove Highlight")
      remove.onAction = EventHandler { e: ActionEvent? -> highlightableTextArea.removeHighlight() }

      val lbl = Label("Resize the window to see if the highlight is moving with text")
      lbl.style = "-fx-font-size: 17px;-fx-font-style:italic;"
      val rb = HBox(remove, lbl)
      rb.spacing = 10.0

      root.children.addAll(hb, rb, highlightableTextArea)
    }
  }

  /**
   * Custom TextArea Component.
   */
  internal class HighlightableTextArea : StackPane() {
    val textArea: TextArea = TextArea()
    var highlightStartPos: Int = -1
    var highlightEndPos: Int = -1
    var highlightInProgress: Boolean = false

    val highlight: Rectangle = Rectangle()

    private val text: StringProperty = SimpleStringProperty()

    private var selectionGroup: Group? = null

    fun getText(): String {
      return text.get()
    }

    fun setText(value: String) {
      text.set(value)
    }

    fun textProperty(): StringProperty {
      return text
    }

    init {
      highlight.fill = Color.RED
      highlight.isMouseTransparent = true
      highlight.blendMode = BlendMode.DARKEN

      textArea.textProperty().bindBidirectional(text)
      children.add(textArea)
      alignment = Pos.TOP_LEFT
      textArea.widthProperty().addListener { obs: ObservableValue<out Number?>?, oldVal: Number?, newVal: Number? ->
        if (highlightStartPos > -1 && highlightEndPos > -1 && selectionGroup != null) {
          highlightInProgress = true
          textArea.selectRange(highlightStartPos, highlightEndPos)
          val bounds = selectionGroup!!.boundsInLocal
          updateHightlightBounds(bounds)
        }
      }
    }

    private fun updateHightlightBounds(bounds: Bounds) {
      if (bounds.width > 0) {
        if (!children.contains(highlight)) {
          children.add(highlight)
        }
        highlight.translateX = bounds.minX + 1
        highlight.translateY = bounds.minY + 1
        highlight.width = bounds.width
        highlight.height = bounds.height
        Platform.runLater {
          textArea.deselect()
          highlightInProgress = false
        }
      }
    }

    override fun layoutChildren() {
      super.layoutChildren()
      if (selectionGroup == null) {
        val content = lookup(".content") as Region
        // Looking for the Group node that is responsible for selection
        content.childrenUnmodifiable.stream().filter { node: Node? -> node is Group }.map { node: Node -> node as Group }
          .filter { grp: Group ->
            val notSelectionGroup = grp.children.stream().anyMatch { node: Node? -> node !is Path }
            !notSelectionGroup
          }.findFirst().ifPresent { n: Group ->
            n.boundsInLocalProperty().addListener { obs: ObservableValue<out Bounds>?, old: Bounds?, bil: Bounds ->
              if (highlightInProgress) {
                updateHightlightBounds(bil)
              }
            }
            selectionGroup = n
          }
      }
    }

    fun highlight(startPos: Int, endPos: Int) {
      highlightInProgress = true
      highlightStartPos = startPos
      highlightEndPos = endPos
      textArea.selectRange(startPos, endPos)
    }

    fun removeHighlight() {
      textArea.deselect()
      children.remove(highlight)
      highlightStartPos = -1
      highlightEndPos = -1
    }
  }
  
  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      Application.launch(Sample::class.java, *args)
    }
  }
}