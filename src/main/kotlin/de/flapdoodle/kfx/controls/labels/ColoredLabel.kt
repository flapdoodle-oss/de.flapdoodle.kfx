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
package de.flapdoodle.kfx.controls.labels

import de.flapdoodle.kfx.bindings.ObjectBindings
import de.flapdoodle.kfx.bindings.syncWith
import de.flapdoodle.kfx.extensions.Colors
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.layout.StackLikeRegion
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.scene.text.TextFlow

class ColoredLabel(
  private val text: ObservableValue<String?>,
  private val colors: ObservableValue<List<Part>>
) : StackLikeRegion() {
  private val textFlow = TextFlow().apply {
    cssClassName("text-flow")
  }
  private val textWithColors = ObjectBindings.merge(text, colors, ::coloredText)

  init {
    textFlow.children.syncWith(textWithColors) {
      Text(it.text).apply {
        val color = it.color
        if (color!=null) {
          fill = color
          style = "-fx-fill: ${Colors.asCss(color)}"
        }
      }
    }
//    textFlow.children.addListener(ListChangeListener { change ->
//      println("textFlow: ${textFlow.children}")
//    })
    children.add(textFlow)
  }

  data class Part(val start: Int, val end: Int, val color: Color)
  data class ColoredText(val index: Int, val text: String, val color: Color? = null)

  companion object {
    fun coloredText(text: String?, parts: List<Part>): List<ColoredText> {
      if (text==null) {
        return emptyList()
      }

      if (parts.isEmpty()) {
        return listOf(ColoredText(0, text))
      }
      
      val sorted = parts.sortedWith(Comparator.comparing(Part::start).thenComparing(Comparator.comparing(Part::end).reversed()))
      val markers = (sorted
        .flatMap { listOf(it.start, it.end) }
        .filter { it < text.length }
        + text.length)
        .toSortedSet()


      var pairs = emptyList<ColoredText>()

      var lastEnd = 0
      var index = 0
      markers.forEach { pos ->
        if (pos > lastEnd) {
          val textPart = text.substring(lastEnd, pos)
          val colors = sorted.filter { it.start < pos && pos <= it.end }
            .map { it.color }
//          println("($lastEnd,$pos) -> $textPart: $colors")
          val color = mixOrNull(colors)

          pairs = pairs + ColoredText(index, textPart, color)
          index++
        }
        lastEnd = pos
      }

//      println("$text, $parts --> $pairs")
      return pairs
    }

    private fun mixOrNull(colors: List<Color>): Color? {
      if (colors.isEmpty()) {
        return null
      }
      if (colors.size == 1) {
        return colors[0]
      }
      return Color.color(
        avg(colors, Color::getRed),
        avg(colors, Color::getGreen),
        avg(colors, Color::getBlue),
        avg(colors, Color::getOpacity),
      )
    }

    private fun avg(list: List<Color>, map: (Color) -> Double): Double {
      return list.map(map).sum() / list.size
    }
  }
}