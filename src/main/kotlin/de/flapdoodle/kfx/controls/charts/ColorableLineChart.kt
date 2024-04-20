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
package de.flapdoodle.kfx.controls.charts

import de.flapdoodle.kfx.extensions.bindCss
import javafx.beans.NamedArg
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.chart.Axis
import javafx.scene.chart.LineChart
import javafx.scene.paint.Color

// max 8 colors
class ColorableLineChart<X, Y>(
  @NamedArg("xAxis") xAxis: Axis<X>,
  @NamedArg("yAxis") yAxis: Axis<Y>,
  @NamedArg("data") data: ObservableList<Series<X, Y>>,
  private val colorMapping: (Series<X, Y>) -> Color
) : LineChart<X, Y>(xAxis, yAxis, data) {
  init {
    bindCss("colorable-line-chart")

    setColors(data)
    
    data.addListener(ListChangeListener {
      setColors(it.list)
    })
  }

  private fun setColors(list: ObservableList<out Series<X, Y>>) {
    if (list.size > 8) {
      IllegalArgumentException("more than 8 series, color mapping does not work as expected")
        .printStackTrace()
    }
    var colors = ""
    list.forEachIndexed { index, serie ->
      val color = colorMapping(serie)
      colors += "-fx-chart-color-$index: ${asCss(color)};\n"
    }
    style = colors
  }

  private fun asCss(color: Color): String {
    return String.format("#%02X%02X%02X", (color.red * 255).toInt(), (color.green * 255).toInt(), (color.blue * 255).toInt())
  }

}